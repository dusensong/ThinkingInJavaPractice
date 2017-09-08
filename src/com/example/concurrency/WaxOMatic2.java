package com.example.concurrency;

import net.mindview.util.Print;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaxOMatic2 {
    static class Car {
        private Lock mLock = new ReentrantLock();
        private Condition mCondition = mLock.newCondition();
        private boolean waxOn = false;

        public void waxed() {
            mLock.lock();
            try {
                waxOn = true; // ready to buff
                mCondition.signalAll();
            } finally {
                mLock.unlock();
            }
        }

        public void buffed() {
            mLock.lock();
            try {
                waxOn = false; // ready to wax
                mCondition.signalAll();
            } finally {
                mLock.unlock();
            }
        }

        public void waitForWaxing() throws InterruptedException {
            mLock.lock();
            try {
                while (waxOn == false)
                    mCondition.await();
            } finally {
                mLock.unlock();
            }

        }

        public void waitForBuffing() throws InterruptedException {
            mLock.lock();
            try {
                while (waxOn == true)
                    mCondition.await();
            } finally {
                mLock.unlock();
            }
        }
    }

    static class WaxOnTask implements Runnable {
        private WaxOMatic.Car mCar;

        public WaxOnTask(WaxOMatic.Car car) {
            mCar = car;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Print.print("Wax On!");
                    TimeUnit.MILLISECONDS.sleep(200);
                    mCar.waxed();
                    mCar.waitForBuffing();
                }
            } catch (InterruptedException e) {
                Print.print("Exist WaxOnTask via InterruptedException");
            }
            Print.print("Ending Wax On Task");
        }
    }

    static class BuffTask implements Runnable {
        private WaxOMatic.Car mCar;

        public BuffTask(WaxOMatic.Car car) {
            mCar = car;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    mCar.waitForWaxing();
                    Print.print("Buff!");
                    TimeUnit.MILLISECONDS.sleep(200);
                    mCar.buffed();
                }
            } catch (InterruptedException e) {
                Print.print("Exist BuffTask via InterruptedException");
            }
            Print.print("Ending Wax Off Task");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        WaxOMatic.Car car = new WaxOMatic.Car();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new WaxOMatic.WaxOnTask(car));
        exec.execute(new WaxOMatic.BuffTask(car));
        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}
