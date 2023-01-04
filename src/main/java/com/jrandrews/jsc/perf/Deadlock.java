package com.jrandrews.jsc.perf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Scenario:
 *  Alice wants to shop; she takes the bag, and wants car, and won't relinquish the bag until she gets back.
 *  Bob wants to wash the car; he takes the car and won't relinquish it until he gets a sponge and finishes washing the car.
 *  Carol wants to take sponges to the neighbor; she takes the sponge, and won't give it back until she gets the bag and comes home.
 * 
 * @param args
 */
public class Deadlock {
    private static ExecutorService alice = Executors.newSingleThreadExecutor();
    private static ExecutorService bob = Executors.newSingleThreadExecutor();
    private static ExecutorService carol = Executors.newSingleThreadExecutor();
    private static final Lock carLock = new ReentrantLock();
    private static final Lock bagLock = new ReentrantLock();
    private static final Lock spongeLock = new ReentrantLock();

    public static class Alice implements Runnable {
        public void run() {
            bagLock.lock();
            sleep(250L);
            carLock.lock();
        }
    }

    public static class Bob implements Runnable {
        public void run() {
            carLock.lock();
            sleep(250L);
            spongeLock.lock();
        }
    }

    public static class Carol implements Runnable {
        public void run() {
            spongeLock.lock();
            sleep(250L);
            bagLock.lock();
        }

    }
    public static void main(String[] args) {
        alice.execute(new Alice());
        bob.execute(new Bob());
        carol.execute(new Carol());
    }

    private static void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
