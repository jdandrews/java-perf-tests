package com.jrandrews.jsc.perf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Scenario: many tasks, and plenty of threads, but they all depend on one resource.
 * 
 * @param args
 */
public class ThreadContention {
    private static ExecutorService threadPool = Executors.newFixedThreadPool(100);
    private static final Lock resourceLock = new ReentrantLock();

    public static class Workload implements Runnable {
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " starting.");
                resourceLock.lock();
                sleep(1000L);
                System.out.println(Thread.currentThread().getName() + " finishing.");
            } finally {
                resourceLock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 100; ++i) {
                threadPool.execute(new Workload());
            }
        } finally {
            threadPool.shutdown();
        }
    }

    private static void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
