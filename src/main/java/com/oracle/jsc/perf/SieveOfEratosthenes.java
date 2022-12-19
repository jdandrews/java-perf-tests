package com.oracle.jsc.perf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Calculates the sieve. Slightly modified from code from:
 * <a href="https://www.geeksforgeeks.org/java-program-for-sieve-of-eratosthenes/">Geeks for Geeks Sieve</a>
 * 
 * This code has been contributed by Amit Khandelwal.
 */
public record SieveOfEratosthenes(int primeLimit) implements Callable<List<Integer>> {
    public SieveOfEratosthenes() {
        this(1000);
    }

    /**
     * Create a boolean array "prime[0..n]" and initialize all entries it as true. A value in prime[i] will
     * finally be false if i is Not a prime, else true.
     * 
     * @param n the size of the initial array.
     * @return a list of primes between 2 and n, inclusive.
     */
    private List<Integer> sieveOfEratosthenes(int n) {
        boolean prime[] = new boolean[n + 1];
        for (int i = 0; i <= n; i++)
            prime[i] = true;

        for (int p = 2; p * p <= n; p++) {
            // If prime[p] is not changed, then it is a prime
            if (prime[p] == true) {
                // Update all multiples of p
                for (int i = p * p; i <= n; i += p)
                    prime[i] = false;
            }

        }

        List<Integer> result = new ArrayList<>();
        // Print all prime numbers
        for (int i = 2; i <= n; i++) {
            if (prime[i] == true)
                result.add(i);

            // slow things down so we can instrument the process and see memory behavior
            if (i % 100000 == 0) {
                sleep(1L);
            }
        }

        return result;
    }

    private void sleep(long n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            // no-op
        }
    }

    // Driver Program to test above function
    public static void main(String args[]) {
        int n = 30;
        System.out.print("Following are the prime numbers ");
        System.out.println("smaller than or equal to " + n);
        SieveOfEratosthenes g = new SieveOfEratosthenes();
        System.out.println(g.sieveOfEratosthenes(n));
    }

    @Override
    public List<Integer> call() throws Exception {
        return sieveOfEratosthenes(primeLimit);
    }
}
