package com.example.concurrency.cache.contract;

public interface Cache<K, V> {

    V get(K key);

    void put(K key, V val);

}
