package com.example.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试同步方法和同步控制块性能差异
 */
public class CriticalSection {
    public static class Pair {
        private int x;
        private int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Pair() {
            this(0, 0);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void incrementX() {
            x++;
        }

        public void incrementY() {
            y++;
        }

        public void checkState() {
            if (x != y) {
                throw new PairValuesNotEqualException();
            }
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public class PairValuesNotEqualException extends RuntimeException {
            public PairValuesNotEqualException() {
                super("Pair values not equal: " + Pair.this);
            }
        }
    }

    static abstract class PairManager {
        AtomicInteger checkCounter = new AtomicInteger(0);
        protected Pair p = new Pair();
        private List<Pair> storage = Collections.synchronizedList(new ArrayList<>());

        public synchronized Pair getPair() {
            // Make a copy to keep the original safe
            return new Pair(p.getX(), p.getY());
        }

        // Assuming this is a time consuming operation
        protected void store(Pair pair) {
            storage.add(pair);
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public abstract void increment();
    }

    /**
     * 同步方法
     */
    static class PairManager1 extends PairManager {
        @Override
        public synchronized void increment() {
            p.incrementX();
            p.incrementY();
            store(getPair());
        }
    }

    /**
     * 同步代码块
     */
    static class PairManager2 extends PairManager {
        @Override
        public void increment() {
            Pair pair;
            synchronized (this) {
                p.incrementX();
                p.incrementY();
                pair = getPair();
            }
            store(pair);
        }
    }

    /**
     * 使用显示锁同步代码块
     */
    static class PairManager3 extends PairManager {
        private Lock lock = new ReentrantLock();

        @Override
        public void increment() {
            Pair pair;
            lock.lock();
            try {
                p.incrementX();
                Thread.yield();
                p.incrementY();
                pair = getPair();
            } finally {
                lock.unlock();
            }
            store(pair);
        }

        @Override
        public Pair getPair() {
            lock.lock();
            try {
                // Make a copy to keep the original safe
                return new Pair(p.getX(), p.getY());
            } finally {
                lock.unlock();
            }
        }
    }


    static class PairManipulator implements Runnable {
        private PairManager pm;

        public PairManipulator(PairManager pm) {
            this.pm = pm;
        }

        @Override
        public void run() {
            while (true) {
                pm.increment();
            }
        }

        @Override
        public String toString() {
            return "Pair: " + pm.getPair() + " checkCounter = " + pm.checkCounter.get();
        }
    }

    static class PairChecker implements Runnable {
        private PairManager pm;

        public PairChecker(PairManager pm) {
            this.pm = pm;
        }

        @Override
        public void run() {
            while (true) {
                pm.checkCounter.incrementAndGet();
                pm.getPair().checkState();
            }
        }
    }

    public static void main(String[] args) {
        PairManager pm1 = new PairManager1();
        PairManager pm2 = new PairManager2();
        PairManager pm3 = new PairManager3();
        testApproaches(pm1, pm2, pm3);
    }

    /**
     * Test the two different approaches
     *
     * @param pman1
     * @param pman2
     */
    static void testApproaches(PairManager pman1, PairManager pman2, PairManager pman3) {
        ExecutorService exec = Executors.newCachedThreadPool();
        PairManipulator pm1 = new PairManipulator(pman1);
        PairManipulator pm2 = new PairManipulator(pman2);
        PairManipulator pm3 = new PairManipulator(pman3);
        PairChecker pairChecker1 = new PairChecker(pman1);
        PairChecker pairChecker2 = new PairChecker(pman2);
        PairChecker pairChecker3 = new PairChecker(pman3);
        exec.execute(pm1);
        exec.execute(pm2);
        exec.execute(pm3);
        exec.execute(pairChecker1);
        exec.execute(pairChecker2);
        exec.execute(pairChecker3);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("pm1: " + pm1 + "\npm2: " + pm2 + "\npm3: " + pm3);
        System.exit(0);
    }
}
