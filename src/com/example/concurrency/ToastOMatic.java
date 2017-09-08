package com.example.concurrency;

import net.mindview.util.Print;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ToastOMatic {
    static class Toast {
        public enum Status {
            DRY, BUTTERED, JAMMED
        }

        private Status mStatus = Status.DRY;
        private final int id;

        public Toast(int id) {
            this.id = id;
        }

        public void butter() {
            mStatus = Status.BUTTERED;
        }

        public void jam() {
            mStatus = Status.JAMMED;
        }

        public Status getStatus() {
            return mStatus;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Toast " + id + ": " + mStatus;
        }
    }

    static class ToastQueue extends LinkedBlockingQueue<Toast> {

    }

    static class Toaster implements Runnable {
        private ToastQueue mToastQueue;
        private int mCount = 0;
        private Random mRand = new Random(47);

        public Toaster(ToastQueue toastQueue) {
            mToastQueue = toastQueue;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    TimeUnit.MILLISECONDS.sleep(100 + mRand.nextInt(500));
                    // Make toast
                    Toast t = new Toast(mCount++);
                    Print.print(t);
                    mToastQueue.add(t);
                }
            } catch (InterruptedException e) {
                Print.print("Toaster interrupted");
            }
            Print.print("Toaster off");
        }
    }

    /**
     * Apply butter to toast
     */
    static class Butterer implements Runnable {
        private ToastQueue mDryQueue;
        private ToastQueue mButteredQueue;

        public Butterer(ToastQueue dryQueue, ToastQueue butteredQueue) {
            mDryQueue = dryQueue;
            mButteredQueue = butteredQueue;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // Blocks until next piece of toast is available
                    Toast t = mDryQueue.take();
                    t.butter();
                    Print.print(t);
                    mButteredQueue.add(t);
                }
            } catch (InterruptedException e) {
                Print.print("Butterer Interruputed");
            }
            Print.print("Butterer off");
        }
    }

    static class Jammer implements Runnable {
        private ToastQueue mButteredQueue;
        private ToastQueue mFinishedQueue;

        public Jammer(ToastQueue butteredQueue, ToastQueue finishedQueue) {
            mButteredQueue = butteredQueue;
            mFinishedQueue = finishedQueue;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // Blocks until next piece of toast is available
                    Toast t = mButteredQueue.take();
                    t.jam();
                    Print.print(t);
                    mFinishedQueue.add(t);
                }
            } catch (InterruptedException e) {
                Print.print("Jamer Interrupted");
            }
            Print.print("Jammer off");
        }
    }

    static class Eater implements Runnable {
        private ToastQueue mFinishedQueue;
        private int mCount = 0;

        public Eater(ToastQueue finishedQueue) {
            mFinishedQueue = finishedQueue;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // Blocks until next piece of toast is available
                    Toast t = mFinishedQueue.take();
                    // Verify that the toast is coming in order and that all pieces are getting jammed
                    if (t.getId() != mCount++ || t.getStatus() != Toast.Status.JAMMED) {
                        Print.print(">>>> Error: " + t);
                        System.exit(1);
                    } else {
                        Print.print("Eat! " + t);
                    }
                }
            } catch (InterruptedException e) {
                Print.print("Eater Interrupted");
            }
            Print.print("Eater off");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ToastQueue mDryQueue = new ToastQueue();
        ToastQueue mButteredQueue = new ToastQueue();
        ToastQueue mFinishedQueue = new ToastQueue();

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new Toaster(mDryQueue));
        exec.execute(new Butterer(mDryQueue, mButteredQueue));
        exec.execute(new Jammer(mButteredQueue, mFinishedQueue));
        exec.execute(new Eater(mFinishedQueue));

        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}
