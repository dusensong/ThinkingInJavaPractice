package com.example.concurrency;

/**
 * 整数生成器
 * Created by BG241996 on 2017/8/31.
 */
public abstract class IntGenerator {
    private volatile boolean cancelled = false;

    public abstract int next();

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
