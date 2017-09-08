package com.example.concurrency;

import net.mindview.util.Print;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class HorseRace {
    static final int FINISH_LINE = 75;
    private List<Horse> horses = new ArrayList<>();
    private ExecutorService exec = Executors.newCachedThreadPool();
    private CyclicBarrier barrier;

    public HorseRace(int nHorses, final int pause) {
        barrier = new CyclicBarrier(nHorses, new Runnable() {
            @Override
            public void run() {
                // print race track
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < FINISH_LINE; i++) {
                    s.append("=");
                }
                s.append(Thread.currentThread());
                Print.print(s);

                // print horse track
                for (Horse horse : horses) {
                    Print.print(horse.tracks());
                }

                for (Horse horse : horses) {
                    if (horse.getStrides() >= FINISH_LINE) {
                        Print.print(horse + " WON!");
                        exec.shutdownNow();
                        return;
                    }
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(pause);
                } catch (InterruptedException e) {
                    Print.print("barrier-action sleep interrupted");
                }
            }
        });
        for (int i = 0; i < nHorses; i++) {
            Horse horse = new Horse(barrier);
            horses.add(horse);
            exec.execute(horse);
        }
    }

    public static void main(String[] args) {
        new HorseRace(5, 200);
    }

    static class Horse implements Runnable {
        private static int counter = 0;
        private final int id = counter++;
        private int strides = 0;
        private static Random random = new Random(47);
        private CyclicBarrier cyclicBarrier;

        public Horse(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        public synchronized int getStrides() {
            return strides;
        }

        @Override
        public void run() {
            Print.print(this + " run in " + Thread.currentThread());
            try {
                while (!Thread.interrupted()) {
                    synchronized (this) {
                        strides += random.nextInt(3);
                    }
                    cyclicBarrier.await();
                }
            } catch (InterruptedException e) {
                // a legal way to exit
            } catch (BrokenBarrierException e) {
                // this one we want to know about
                throw new RuntimeException();
            }
        }

        @Override
        public String toString() {
            return "Horse " + id;
        }

        public String tracks() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < getStrides(); i++) {
                s.append("*");
            }
            s.append(id);
            return s.toString();
        }
    }
}
