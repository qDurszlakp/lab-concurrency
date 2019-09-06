package com.example.concurrency.communication.impl;

import com.example.concurrency.communication.base.ProducerConsumer;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockAndCondition extends ProducerConsumer {

    private static Lock lock = new ReentrantLock();

    private static Condition isEmptyLock = lock.newCondition();
    private static Condition isFullLock = lock.newCondition();


    @Override
    protected void produce(List<Integer> buffer, int threshold) throws InterruptedException {
        try {
            lock.lock();
            while (isFull(buffer, threshold)) {
                isFullLock.await();
            }
            buffer.add(1);
            isEmptyLock.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void consume(List<Integer> buffer) throws InterruptedException {
        try {
            lock.lock();
            while (isEmpty(buffer)) {
                isEmptyLock.await();
            }
            buffer.remove(buffer.size() -1);
            isFullLock.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
