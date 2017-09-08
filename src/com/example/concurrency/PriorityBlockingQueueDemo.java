package com.example.concurrency;

import net.mindview.util.Print;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.*;

public class PriorityBlockingQueueDemo {
    static class PrioritizedTask implements Runnable, Comparable<PrioritizedTask> {
        private static int counter = 0;
        private final int id = counter++;
        private final int priority;
        protected static List<PrioritizedTask> sequence = new ArrayList<>();

        public PrioritizedTask(int priority) {
            this.priority = priority;
            sequence.add(this);
        }

        @Override
        public int compareTo(PrioritizedTask right) {
            return priority < right.priority ? 1 : (priority > right.priority ? -1 : 0);
        }

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Print.print("Run " + this);
        }

        @Override
        public String toString() {
            return String.format("[%d] Task %d", priority, id);
        }

        public String summary() {
            return "(" + id + ":" + priority + ")";
        }
    }

    static class EndSentinel extends PrioritizedTask {
        private ExecutorService exec;

        public EndSentinel(ExecutorService e) {
            super(-1);
            exec = e;
        }

        @Override
        public void run() {
            for (PrioritizedTask pt : sequence) {
                Print.print(pt.summary());
            }
            Print.print(this + " Calling shutdownNow()");
            exec.shutdownNow();
        }
    }

    static class PrioritizedTaskProducer implements Runnable {
        private Random rand = new Random(47);
        PriorityBlockingQueue<PrioritizedTask> queue;
        private ExecutorService exec;

        public PrioritizedTaskProducer(PriorityBlockingQueue<PrioritizedTask> q, ExecutorService e) {
            this.queue = q;
            this.exec = e;
        }

        @Override
        public void run() {
            // Fill it up fast with random priorities
            for (int i = 0; i < 10; i++) {
                queue.add(new PrioritizedTask(rand.nextInt(10)));
                Thread.yield();
            }

            try {
                // Add highest-priority jobs
                for (int i = 0; i < 5; i++) {
                    TimeUnit.MILLISECONDS.sleep(200);
                    queue.add(new PrioritizedTask(10));
                }

                // add jobs, lowest priority first
                for (int i = 0; i < 5; i++) {
                    queue.add(new PrioritizedTask(i));
                }

                // A sentinel to stop all the task
                queue.add(new EndSentinel(exec));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Print.print("Finished PrioritizedTaskProducer");
        }
    }

    static class PrioritizedTaskConsumer implements Runnable {
        PriorityBlockingQueue<PrioritizedTask> queue;

        public PrioritizedTaskConsumer(PriorityBlockingQueue<PrioritizedTask> q) {
            this.queue = q;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    PrioritizedTask task = queue.take();
//                    Print.print("Taken a Priority Task: " + task);
                    task.run(); // Run task with current thread
                }
            } catch (InterruptedException e) {
                // Acceptable way to exit
            }
            Print.print("Finished PrioritizedTaskConsumer");
        }
    }

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        PriorityBlockingQueue<PrioritizedTask> queue = new PriorityBlockingQueue<>();
        exec.execute(new PrioritizedTaskProducer(queue, exec));
        exec.execute(new PrioritizedTaskConsumer(queue));
    }
}
