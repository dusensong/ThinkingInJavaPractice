package com.example.concurrency;

import net.mindview.util.Print;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用显示锁实现中断锁阻塞
 */
public class Interrupting2 {
    static class BlockedMutex {
        private Lock mLock = new ReentrantLock();

        public BlockedMutex() {
            mLock.lock();
        }

        public void f() {
            try {
                mLock.lockInterruptibly();
                Print.print("Lock acquired in f()");
            } catch (InterruptedException e) {
                Print.print("Interrupted from lock acquisition in f()");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final BlockedMutex blockedMutex = new BlockedMutex();
        Thread t = new Thread() {
            @Override
            public void run() {
                Print.print("Waiting for f() in BlockedMutex");
                blockedMutex.f();
                Print.print("Broken out of blocked call");
            }
        };
        t.start();
        TimeUnit.SECONDS.sleep(1);
        Print.print("Issuing t.interrupt");
        t.interrupt();
    }
}
