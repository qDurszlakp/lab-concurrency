package com.example.concurrency.cache.impl;

import com.example.concurrency.cache.contract.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockCache<K, V> implements Cache<K, V> {

    private Map<K, V> cache = new HashMap<>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    @Override
    public V get(K key) {
        readLock.lock();
        try {
            return cache.get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void put(K key, V val) {
        writeLock.lock();
        try {
            cache.put(key, val);
        } finally {
            writeLock.unlock();
        }
    }

}
