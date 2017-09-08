package com.example.concurrency;

import net.mindview.util.Print;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestBlockingQueues {
    static class LiftOffRunner implements Runnable {
        private BlockingQueue<LiftOff> mRockets;

        public LiftOffRunner(BlockingQueue<LiftOff> rockets) {
            mRockets = rockets;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    LiftOff rocket = mRockets.take();
                    rocket.run();
                }
            } catch (InterruptedException e) {
                Print.print("Interrupted from take()");
            }
            Print.print("Exiting LiftOffRunner");
        }

        public void add(LiftOff rocket) {
            try {
                mRockets.put(rocket);
            } catch (InterruptedException e) {
                Print.print("Interrupted from put()");
            }
        }
    }

    static void getKey() {
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static void test(String msg, BlockingQueue<LiftOff> queue) {
        Print.print(msg);
        LiftOffRunner liftOffRunner = new LiftOffRunner(queue);
        Thread t = new Thread(liftOffRunner);
        t.start();
        for (int i = 0; i < 3; i++) {
            liftOffRunner.add(new LiftOff(5));
        }
        getKey(); // block main thread by io
        t.interrupt();
        Print.print("Finished " + msg + " test");
    }

    public static void main(String[] args) {
        test("LinkedBlockingQueue", new LinkedBlockingQueue<>());
    }
}
