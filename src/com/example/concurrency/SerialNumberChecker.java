package com.example.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试原子性问题
 */
public class SerialNumberChecker {
    private static final int SIZE = 10;
    private static CircularSet serials = new CircularSet(1000);
    private static ExecutorService exec = Executors.newCachedThreadPool();

    /**
     * Reuses storage so we don't run out of memory
     */
    static class CircularSet {
        private int[] array;
        private int len;
        private int index = 0;

        public CircularSet(int size) {
            array = new int[size];
            len = size;
            // initialize default value
            for (int i = 0; i < array.length; i++) {
                array[i] = -1;
            }
        }

        public synchronized void add(int i) {
            array[index] = i;
            index = ++index % len;
        }

        public synchronized boolean contains(int val) {
            for (int i = 0; i < len; ++i)
                if (array[i] == val) return true;
            return false;
        }
    }

    static class SerialNumberGenerator {
        private static volatile int serialNumber = 0;

        public static int nextSerialNumber() {
            return serialNumber++;
        }
    }


    static class SerialChecker implements Runnable {
        @Override
        public void run() {
            while (true) {
                // nextSerialNumber()方法不具有原子性，因此会产生并发问题
                int serial = SerialNumberGenerator.nextSerialNumber();
                if (serials.contains(serial)) {
                    System.out.println("Duplicate: " + serial);
                    System.exit(0);
                }
                serials.add(serial);
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < SIZE; i++) {
            exec.execute(new SerialChecker());
        }
    }
}
