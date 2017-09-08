package com.example.concurrency;

import net.mindview.util.Print;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NotifyVsNotifyAll {

    static class Blocker {
        synchronized void waitingCall() {
            try {
                while (!Thread.interrupted()) {
                    wait();
                    Print.print(Thread.currentThread());
                }
            } catch (InterruptedException e) {
            }
        }

        synchronized void prod() {
            notify();
        }

        synchronized void prodAll() {
            notifyAll();
        }
    }

    static class Task1 implements Runnable {
        static Blocker sBlocker = new Blocker();

        @Override
        public void run() {
            sBlocker.waitingCall();
        }
    }

    static class Task2 implements Runnable {
        static Blocker sBlocker = new Blocker();

        @Override
        public void run() {
            sBlocker.waitingCall();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new Task1());
        exec.execute(new Task1());
        exec.execute(new Task2());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            boolean prod = false;

            @Override
            public void run() {
                if (prod) {
                    Print.print("\nTask1.sBlocker.prod()");
                    Task1.sBlocker.prod();
                    prod = false;
                } else {
                    Print.print("\nTask1.sBlocker.prodAll()");
                    Task1.sBlocker.prodAll();
                    prod = true;
                }
            }
        }, 400, 400);
        TimeUnit.SECONDS.sleep(5);
        timer.cancel();
        Print.print("\nTimer cancelled");

        TimeUnit.MILLISECONDS.sleep(500);
        Print.print("\nTask2.sBlocker.prodAll()");
        Task2.sBlocker.prodAll();

        TimeUnit.MILLISECONDS.sleep(500);
        Print.print("\nShutting down");
        exec.shutdownNow();
    }
}
