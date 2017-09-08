package com.example.concurrency;

import net.mindview.util.Print;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WaxOMatic {
    static class Car {
        private boolean waxOn = false;

        public synchronized void waxed() {
            waxOn = true;
            notifyAll();
        }

        public synchronized void buffed() {
            waxOn = false;
            notifyAll();
        }

        public synchronized void waitForWaxing() throws InterruptedException {
            while (waxOn == false)
                wait();
        }

        public synchronized void waitForBuffing() throws InterruptedException {
            while (waxOn == true)
                wait();
        }
    }

    static class WaxOnTask implements Runnable {
        private Car mCar;

        public WaxOnTask(Car car) {
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
        private Car mCar;

        public BuffTask(Car car) {
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
        Car car = new Car();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new WaxOnTask(car));
        exec.execute(new BuffTask(car));
        TimeUnit.SECONDS.sleep(5);
        exec.shutdownNow();
    }
}
