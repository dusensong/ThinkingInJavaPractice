package com.example.concurrency;

import net.mindview.util.Print;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchDemo {
    static class TaskPortion implements Runnable {
        private static int counter = 0;
        private final int id = counter++;
        private CountDownLatch latch;
        private Random random = new Random(47);

        public TaskPortion(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                doWork();
                latch.countDown();
            } catch (InterruptedException e) {
                // Acceptable way to exit
            }
        }

        private void doWork() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(2000));
            Print.print(this + " Completed");
        }

        @Override
        public String toString() {
            return String.format("TaskPortion %3d", id);
        }
    }

    static class WaitingTask implements Runnable {
        private static int counter = 0;
        private final int id = counter++;
        private CountDownLatch latch;

        public WaitingTask(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                latch.await();
                Print.print("Latch barrier passed for " + this);
            } catch (InterruptedException e) {
                Print.print(this + " interrupted");
            }
        }

        @Override
        public String toString() {
            return String.format("WaitingTask %3d", id);
        }
    }

    static final int SIZE = 5;

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(SIZE);
        for (int i = 0; i < 2; i++) {
            exec.execute(new WaitingTask(latch));
        }
        for (int i = 0; i < SIZE; i++) {
            exec.execute(new TaskPortion(latch));
        }
        Print.print("Launched all tasks");
        exec.shutdown(); // Quit when all tasks complete
    }
}
