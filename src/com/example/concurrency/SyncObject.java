package com.example.concurrency;

import net.mindview.util.Print;

public class SyncObject {
    static class DualSynch {
        private Object syncObject = new Object();

        public synchronized void f() {
            for (int i = 0; i < 5; i++) {
                Print.print("f()");
                Thread.yield();
            }
        }

        public void g() {
            synchronized (syncObject) {
                for (int i = 0; i < 5; i++) {
                    Print.print("g()");
                    Thread.yield();
                }
            }
        }
    }

    public static void main(String[] args) {
        DualSynch dualSynch = new DualSynch();
        new Thread() {
            @Override
            public void run() {
                dualSynch.f();
            }
        }.start();
        dualSynch.g();
    }
}
