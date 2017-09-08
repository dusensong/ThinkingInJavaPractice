package com.example.concurrency;

import net.mindview.util.Print;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrnamentalGarden {
    static class Count {
        private int mCount = 0;
        private Random mRandom = new Random(47);

        public synchronized int increment() {
            int temp = mCount;
            if (mRandom.nextBoolean()) {
                Thread.yield();
            }
            return (mCount = ++temp);
        }

        public synchronized int value() {
            return mCount;
        }
    }

    /**
     * thread-safe class
     */
    static class Entrance implements Runnable {
        private static Count sCount = new Count();
        private static List<Entrance> sEntrances = new ArrayList<>();
        private static volatile boolean cancelled = false;
        private int number = 0;
        // Doesn't need synchronization to read
        private final int id;

        public Entrance(int id) {
            this.id = id;
            // Keep thi task in a list.Also prevents garbage collection of dead tasks
            sEntrances.add(this);
        }

        @Override
        public void run() {
            while (!cancelled) {
                synchronized (this) {
                    ++number;
                }
                Print.print(this + " Total: " + sCount.increment());
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Print.print("sleep interrupted");
                }
            }
            Print.print("Stopping " + this);
        }

        public synchronized int getValue() {
            return number;
        }

        // Atomic operation on a volatile field
        public static void cancel() {
            cancelled = true;
        }

        public static int getTotalCount() {
            return sCount.value();
        }

        public static int sumEntrances() {
            int sum = 0;
            for (Entrance entrance : sEntrances) {
                sum += entrance.getValue();
            }
            return sum;
        }

        @Override
        public String toString() {
            return "Entrance[" + id + "]: " + getValue();
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.execute(new Entrance(i));
        }
        // Run for a while, then stop and collect the data
        TimeUnit.SECONDS.sleep(10);
        Entrance.cancel();
        exec.shutdown();
        if (!exec.awaitTermination(250, TimeUnit.MILLISECONDS))
            Print.print("Some tasks were not terminated!");
        Print.print("Total: " + Entrance.getTotalCount());
        Print.print("Sum of Entrances: " + Entrance.sumEntrances());
    }
}
