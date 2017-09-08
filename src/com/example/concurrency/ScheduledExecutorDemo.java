package com.example.concurrency;

import net.mindview.util.Print;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorDemo {
    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

        // 5秒后执行TaskA
        Print.print("TaskA will run after 5s of " + getCurrentTime());
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                Print.print("TaskA running in " + Thread.currentThread() + " at " + getCurrentTime());
            }
        }, 5, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(6);

        // 2秒后开始执行TaskB，每隔5秒执行一次
        Print.print("TaskB start run after 2s of " + getCurrentTime());
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Print.print("TaskB running in " + Thread.currentThread() + " at " + getCurrentTime());
            }
        }, 2, 5, TimeUnit.SECONDS);
    }


    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH时mm分ss秒");
        return sdf.format(System.currentTimeMillis());
    }
}
