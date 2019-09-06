package com.example.concurrency;

import java.util.Arrays;

/**
 * Preview of False Sharing drawback resulting from the construction of the modern CPU.
 * Dirty marking on L1 cache as an example.
 */
public class FalseSharing {

    private static int NUM_THREADS_MAX = 4;
    private final static long ITERATIONS = 50_000_000L;

    private static VolatileLongPadded[] volatileLongPaddeds;
    private static VolatileLongUnPadded[] volatileLongUnPaddeds;

    private final static class VolatileLongPadded {
        private long q1, q2, q3, q4, q5, q6;
        private volatile long value = 0L;
        private long q11, q22, q33, q44, q55, q66;
    }

    private final static class VolatileLongUnPadded {
        private volatile long value = 0L;
    }

    static {
        volatileLongPaddeds = new VolatileLongPadded[NUM_THREADS_MAX];
        Arrays.setAll(volatileLongPaddeds, i -> new VolatileLongPadded());

        volatileLongUnPaddeds = new VolatileLongUnPadded[NUM_THREADS_MAX];
        Arrays.setAll(volatileLongUnPaddeds, i -> new VolatileLongUnPadded());
    }

    public static void runBenchmarks(){

        for (int i = 1; i <= NUM_THREADS_MAX; i++) {

            Thread[] threads = new Thread[i];

            Arrays.setAll(threads, j -> new Thread(createPaddedRunnable(j)));
            long paddedStructureTimeExecution = benchmark(threads);
            System.out.println(String.format("Padded Structure Time Execution: %d", paddedStructureTimeExecution));

            Arrays.setAll(threads, j -> new Thread(createUnPaddedRunnable(j)));
            long unPaddedStructureTimeExecution = benchmark(threads);
            System.out.println(String.format("Un Padded Structure Time Execution: %d", unPaddedStructureTimeExecution));
        }
    }

    private static long benchmark(Thread[] threads) {

        long begin, end;

        begin = System.currentTimeMillis();
        Arrays.stream(threads).forEach(Thread::start);
        Arrays.stream(threads).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        end = System.currentTimeMillis();

        return end - begin;
    }

    private static Runnable createUnPaddedRunnable(int k) {
        return () -> {
            long i = ITERATIONS + 1;
            while (0 != --i) {
                volatileLongUnPaddeds[k].value = i;
            }
        };
    }

    private static Runnable createPaddedRunnable(int k) {
        return () -> {
            long i = ITERATIONS + 1;
            while (0 != --i) {
                volatileLongPaddeds[k].value = i;
            }
        };
    }
}