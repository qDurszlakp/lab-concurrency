package com.example.concurrency.communication.impl;

import com.example.concurrency.communication.base.ProducerConsumer;

import java.util.List;

public class WaitAndNotify extends ProducerConsumer {

    private static final Object lock = new Object();

    @Override
    protected void produce(List<Integer> buffer, int threshold) throws InterruptedException {
        synchronized (lock) {
            while (isFull(buffer, threshold)) {
                lock.wait();
            }
            buffer.add(1);
            lock.notifyAll();
        }
    }

    @Override
    protected void consume(List<Integer> buffer) throws InterruptedException {
        synchronized (lock) {
            while (isEmpty(buffer)) {
                lock.wait();
            }
            buffer.remove(buffer.size() - 1);
            lock.notifyAll();
        }
    }
}
