package com.example.concurrency.communication.impl;

import com.example.concurrency.communication.base.ProducerConsumer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class LockAndConditionTest {

    @Test
    public void shouldReturnOnlyZeroValues() throws InterruptedException {

        int BUFFER_THRESHOLD = 50;
        int PRODUCER_TASKS_COUNT = 100;
        int CONSUMER_TASKS_COUNT = 100;

        ProducerConsumer producerConsumer = new LockAndCondition();

        List<Integer> expected = new ArrayList<>();
        IntStream.range(0, 100).forEach(i -> expected.add(0));

        List<Integer> results = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            results.add(producerConsumer.process(
                    PRODUCER_TASKS_COUNT,
                    CONSUMER_TASKS_COUNT,
                    BUFFER_THRESHOLD));
        }

        Assert.assertEquals(expected, results);
    }

}