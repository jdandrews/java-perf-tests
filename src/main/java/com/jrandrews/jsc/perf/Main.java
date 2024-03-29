package com.jrandrews.jsc.perf;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.jrandrews.jsc.perf.mbean.Hello;

public class Main {
    // We run a Sieve of Eratosthenes to chew up memory, then release it. We run it on its own thread so we can
    // process user inputs while it's running.
    private static final ExecutorService sieveExec = Executors.newSingleThreadExecutor();

    // permanent storage for some data, so we can permanently leak memory when we want to.
    private static final List<SieveResult> memoryLeak = new ArrayList<>();

    // temporary storage for some data, so we can temporarily leak memory.
    private static final PythagoreanCup<List<Integer>> memoryChurner = new PythagoreanCup<>(10);

    // this would be perfect as a record, but it needs to work under Java 8.
    public static class SieveResult {
        private final Duration elapsed;
        private final String message;
        private final List<Integer> primes;

        public SieveResult(Duration elapsed, String message, List<Integer> primes) {
            this.elapsed = elapsed;
            this.message = message;
            this.primes = primes;
        }

        public Duration getElapsed() {
            return elapsed;
        }

        public String getMessage() {
            return message;
        }

        public List<Integer> getPrimes() {
            return primes;
        }
    }

    private static final int NUMBER_OF_PRIMES_TO_SAVE = 50_000;
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        initMbean();

        Instant start = Instant.now();

        boolean leak = args.length > 0 && args[0].equalsIgnoreCase("leak");

        log("Hello, world." + (leak ? " Now leaking memory." : ""));

        // this loops until it gets a newline input from the console.
        // each time through the loop, the sieve is run on 1 -> 10,000,000. The sieve allocates "n" booleans,
        // then returns an arraylist containing the primes: a bit over 5.7M Integers. We shave off a few entries
        // from that list if we're leaking, and store them in the static memoryLeak array above. Each iteration
        // takes about half-second, which gives you an idea of how fast the sieve really is.

        log("press <enter> to exit (heh).");
        for (int i = 0, c = 0; c != 10; ++i) {
            Future<List<Integer>> sieve = (sieveExec.submit(new SieveOfEratosthenes(10_000_000)));

            // This could be done by delegating the whole outer loop to thread, and this to a different thread, and synchronizing,
            // but I think this approach is a little more transparent.
            while (!sieve.isDone()) {
                if (System.in.available() > 0)
                    c = System.in.read();
                sleep(5);
                if (c == 10) {
                    sieve.cancel(true);
                }
            }
            if (sieve.isCancelled())
                continue;

            List<Integer> primes = sieve.get();
            if (i % 50 == 0)
                log(primes.size() + " primes.");

            memoryChurner.add(primes);
            if (leak) {
                String message = primes.size() + " primes";
                // randomize the chunk of the final list of primes to save
                int n1 = random.nextInt(primes.size() - NUMBER_OF_PRIMES_TO_SAVE);
                List<Integer> savedPrimes = new ArrayList<>(primes.subList(n1, n1 + NUMBER_OF_PRIMES_TO_SAVE));

                memoryLeak.add(new SieveResult(Duration.between(start, Instant.now()), message, savedPrimes));
            }
        }

        sieveExec.shutdown();
    }

    private static void initMbean() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName("com.jrandrews.jsc.perf.mbean:type=Hello");
            Hello mbean = new Hello();
            mbs.registerMBean(mbean, name);

        } catch ( MalformedObjectNameException
                | InstanceAlreadyExistsException
                | MBeanRegistrationException
                | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    private static void log(String s) {
        System.out.println(s);
    }

    private static void sleep(long n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            // no-op
        }
    }
}
