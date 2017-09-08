package com.example.concurrency;

import net.mindview.util.Print;

import java.util.concurrent.TimeUnit;

/**
 * 中断线程正确实现
 */
public class InterruptingIdiom {

    static class NeedCleanup {
        private final int id;

        public NeedCleanup(int id) {
            this.id = id;
            Print.print("NeedsCleanup " + id);
        }

        public void cleanup() {
            Print.print("Cleaning up " + id);
        }
    }

    static class Blocked implements Runnable {
        private volatile double d = 0.0;

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // point1
                    NeedCleanup n1 = new NeedCleanup(1);
                    // Start try-finally immediately after definition of n1, to guarantee proper cleanup of n1
                    try {
                        Print.print("Sleeping");
                        TimeUnit.SECONDS.sleep(2);
                        // point2
                        NeedCleanup n2 = new NeedCleanup(2);
                        // Guarantee proper cleanup of n2
                        try {
                            Print.print("Calculating");
                            // A time-consuming, non-blocking operation
                            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                                d = d + (Math.PI + Math.E) / d;
                            }
                            Print.print("Finished time-consuming operation");
                        } finally {
                            n2.cleanup();
                        }
                    } finally {
                        n1.cleanup();
                    }
                }
                Print.print("Exist via while()");
            } catch (InterruptedException e) {
                Print.print("Exist via InterruptedException");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            Print.print("Usage: java interruptingIdiom delay-in-ms");
            System.exit(1);
        }

        Thread t = new Thread(new Blocked());
        t.start();
        TimeUnit.MILLISECONDS.sleep(new Integer(args[0]));
        t.interrupt();
    }
}
