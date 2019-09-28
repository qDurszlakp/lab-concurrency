package com.example.concurrency.multitasking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentAbacus {

    static class Abacus implements Callable<String> {

        private CyclicBarrier barrier;

        private int miliSec;

        Abacus(CyclicBarrier barrier, int miliSec) {
            this.barrier = barrier;
            this.miliSec = miliSec;
        }

        @Override
        public String call() {

            try {
                Thread.sleep(miliSec);
                System.out.println("I have finished my calculation.Thread: " + Thread.currentThread().getName());
                barrier.await();
            } catch (InterruptedException  | BrokenBarrierException  e) {
                System.out.println(String.format("Exception: %s, on Abacus.", e.getClass()));
            }

            return "OK";
        }
    }

    static class Gatherer implements Callable<List<String>> {

        List<Future<String>> futures;

        @SafeVarargs
        Gatherer(Future<String>... futures) {
            this.futures = new ArrayList<>();
            Collections.addAll(this.futures, futures);
        }

        @Override
        public List<String> call() {

            List<String> gatheredValues = new ArrayList<>();

            this.futures.forEach(future -> {
                try {
                    gatheredValues.add(future.get(1000, TimeUnit.MILLISECONDS));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    System.out.println(String.format("Exception: %s, on Gatherer.", e.getClass()));
                }
            });
            return gatheredValues;
        }
    }

    public static void main(String[] args) {

        CyclicBarrier barrier = new CyclicBarrier(2, () -> System.out.println("Release barrier lock"));

        Abacus task1 = new Abacus(barrier, 100);
        Abacus task2 = new Abacus(barrier, 2000);

        ExecutorService service = Executors.newFixedThreadPool(2);

        try {
            Future<String> visitor_1 = service.submit(task1);
            Future<String> visitor_2 = service.submit(task2);

            Gatherer gatherer = new Gatherer(visitor_1, visitor_2);

            System.out.println(service.submit(gatherer).get());

        } catch (InterruptedException | ExecutionException e) {
            System.out.println(String.format("Exception: %s, in Main Class.", e.getClass()));
        } finally {
            service.shutdown();
        }
    }
}