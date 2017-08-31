package com.example.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 偶数检查器
 * Created by BG241996 on 2017/8/31.
 */
public class EvenChecker implements Runnable {
    private final int id;
    private IntGenerator mIntGenerator;

    public EvenChecker(int id, IntGenerator intGenerator) {
        this.id = id;
        mIntGenerator = intGenerator;
    }

    @Override
    public void run() {
        while (!mIntGenerator.isCancelled()) {
            int val = mIntGenerator.next();
            if (val % 2 != 0) {
                System.out.println(val + " not even");
                mIntGenerator.cancel();
            }
        }
    }

    public static void test(IntGenerator intGenerator, int count) {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < count; ++i) {
            exec.execute(new EvenChecker(i, intGenerator));
        }
        exec.shutdown();
    }

    public static void test(IntGenerator intGenerator) {
        test(intGenerator, 10);
    }
}
