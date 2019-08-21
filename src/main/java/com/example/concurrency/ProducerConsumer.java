package com.example.concurrency;

import java.util.stream.IntStream;

/**
 * Preview of Producer & Consumer pattern implementation in concurrent world.
 *
 */
public class ProducerConsumer {

    private static final Object lock = new Object();

    private static int[] buffer;
    private static int counter;

    private static class Producer {
        void produce() {
            synchronized (lock) {
                if (isFull(buffer)) {
                    waitOn(lock);
                }
                buffer[counter++] = 1;
                lock.notifyAll();
            }
        }
    }

    private static class Consumer {
        void consume() {
            synchronized (lock) {
                if (isEmpty(buffer)) {
                    waitOn(lock);
                }
                buffer[--counter] = 0;
                lock.notifyAll();
            }
        }
    }

    private static boolean isFull(int[] buffer) {
        return counter == buffer.length;
    }

    private static boolean isEmpty(int[] buffer) {
        return buffer[0] == 0;
    }

    private static void waitOn(Object object) {
        try {
            object.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void run() throws InterruptedException {

        buffer = new int[10];
        counter = 0;

        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        Runnable produceTask = () -> {
            IntStream.range(0, 50).forEach(i -> producer.produce());
            System.out.println("Done producing!");
        };

        Runnable consumerTask = () -> {
            IntStream.range(0, 48).forEach(i -> consumer.consume());
            System.out.println("Done consuming!");
        };

        Thread producerThread = new Thread(produceTask);
        Thread consumerThread = new Thread(consumerTask);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        System.out.println("Buffer data left: " + counter);
    }
}