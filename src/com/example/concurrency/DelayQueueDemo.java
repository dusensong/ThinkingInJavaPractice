package com.example.concurrency;

import net.mindview.util.Print;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class DelayQueueDemo {
    static class DelayedTask implements Runnable, Delayed {
        private static int counter = 0;
        private final int id = counter++;
        private final int delta;
        private final long trigger;
        protected static List<DelayedTask> sequence = new ArrayList<>();

        public DelayedTask(int delayInMilliseconds) {
            this.delta = delayInMilliseconds;
            this.trigger = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delta, TimeUnit.MILLISECONDS);
            sequence.add(this);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long delay = unit.convert(trigger - System.nanoTime(), TimeUnit.NANOSECONDS);
            Print.print(this + " getDelay():" + delay);
            return delay;
        }

        @Override
        public int compareTo(Delayed arg) {
            DelayedTask that = (DelayedTask) arg;
            if (trigger < that.trigger) return -1;
            if (trigger > that.trigger) return 1;
            return 0;
        }

        @Override
        public void run() {
            Print.print(this);
        }

        @Override
        public String toString() {
            return String.format("[%4d] Task %d", delta, id);
        }

        public String summary() {
            return "(" + id + ":" + delta + ")";
        }
    }

    static class EndSentinel extends DelayedTask {
        private ExecutorService exec;

        public EndSentinel(int delayInMilliseconds, ExecutorService e) {
            super(delayInMilliseconds);
            exec = e;
        }

        @Override
        public void run() {
            for (DelayedTask pt : sequence) {
                Print.print(pt.summary());
            }
            Print.print(this + " Calling shutdownNow()");
            exec.shutdownNow();
        }
    }

    static class DelayedTaskConsumer implements Runnable {
        private DelayQueue<DelayedTask> q;

        public DelayedTaskConsumer(DelayQueue<DelayedTask> q) {
            this.q = q;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    DelayedTask task = q.take();
                    Print.print("Taken a Delay Task: " + task);
                    task.run(); // Run task with current thread
                }
            } catch (InterruptedException e) {
                // Acceptable way to exit
            }
            Print.print("Finished PrioritizedTaskConsumer");
        }
    }

    public static void main(String[] args) {
        Random rand = new Random(47);
        ExecutorService exec = Executors.newCachedThreadPool();
        DelayQueue<DelayedTask> queue = new DelayQueue<>();
        // Fill with tasks that have random delays
        for (int i = 0; i < 10; i++) {
            queue.put(new DelayedTask(rand.nextInt(20000)));
        }
        // Set the stopping point
        queue.add(new EndSentinel(20000, exec));
        exec.execute(new DelayedTaskConsumer(queue));
    }
}
