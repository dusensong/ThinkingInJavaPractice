package com.example.concurrency;

import net.mindview.util.Print;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DeadlockingDiningPhilosophers {
    static class Chopstick {
        private boolean taken = false;

        public synchronized void take() throws InterruptedException {
            while (taken) {
                wait();
            }
            taken = true;
        }

        public synchronized void drop() {
            taken = false;
            notifyAll();
        }
    }

    static class Philosopher implements Runnable {
        private Chopstick left;
        private Chopstick right;
        private final int id;
        private final int ponderFactor;

        public Philosopher(Chopstick left, Chopstick right, int id, int ponderFactor) {
            this.left = left;
            this.right = right;
            this.id = id;
            this.ponderFactor = ponderFactor;
        }

        private void pause() throws InterruptedException {
            if (ponderFactor == 0) return;
            TimeUnit.MILLISECONDS.sleep(ponderFactor * 250);
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Print.print(this + " thinking");
                    pause();
                    // philosopher become hungry
                    Print.print(this + " grabbing right");
                    right.take();
                    Print.print(this + " grabbing left");
                    left.take();
                    Print.print(this + " eating");
                    pause();
                    right.drop();
                    left.drop();
                }
            } catch (InterruptedException e) {
                Print.print(this + " exiting via interrupt");
            }
        }

        @Override
        public String toString() {
            return "Philosopher " + id;
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        int ponder = 5;
        if (args.length > 0) {
            ponder = Integer.valueOf(args[0]);
        }
        int size = 5;
        if (args.length > 1) {
            size = Integer.valueOf(args[1]);
        }
        ExecutorService exec = Executors.newCachedThreadPool();
        Chopstick[] chopsticks = new Chopstick[size];
        for (int i = 0; i < size; i++) {
            chopsticks[i] = new Chopstick();
        }
        for (int i = 0; i < size; i++) {
            if(i < (size - 1)){
                exec.execute(
                        new Philosopher(chopsticks[i], chopsticks[i + 1], i, ponder));
            }else{
                exec.execute(
                        new Philosopher(chopsticks[0], chopsticks[i], i, ponder));
            }
        }
        if (args.length == 3 && args[2].equals("timeout")) {
            TimeUnit.SECONDS.sleep(3);
        } else {
            Print.print("Press 'Enter' to quit");
            System.in.read();
        }
        exec.shutdownNow();
    }

}
