package com.example.concurrency;

/**
 * 偶数生成器
 * Created by BG241996 on 2017/8/31.
 */
public class EvenGenerator extends IntGenerator {
    private int currentEvenValue = 0;

    @Override
    public int next() {
        ++currentEvenValue;
        ++currentEvenValue;
        return currentEvenValue;
    }

    public static void main(String[] args) {
        System.out.println("Start EvenChecker Test");
        EvenChecker.test(new EvenGenerator());
    }
}
