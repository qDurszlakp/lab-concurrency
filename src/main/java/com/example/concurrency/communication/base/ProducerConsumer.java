package com.example.concurrency.communication.base;

import java.util.ArrayList;
import java.util.List;

public abstract class ProducerConsumer {

    public int process(int producerTasks, int consumerTasks, int threshold) throws InterruptedException {

        List<Integer> buffer = new ArrayList<>();

        Runnable producerTask = () -> {
            for (int i = 0; i < producerTasks; i++) {
                try {
                    produce(buffer, threshold);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable consumerTask = () -> {
            for (int i = 0; i < consumerTasks; i++) {
                try {
                    consume(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread producerThread = new Thread(producerTask);
        Thread consumerThread = new Thread(consumerTask);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        return buffer.size();
    }

    protected boolean isFull(List<Integer> buffer, int threshold) {
        return buffer.size() == threshold;
    }

    protected boolean isEmpty(List<Integer> buffer) {
        return buffer.isEmpty();
    }

    protected abstract void produce(List<Integer> buffer, int threshold) throws InterruptedException;

    protected abstract void consume(List<Integer> buffer) throws InterruptedException;
}
