package com.mayee.config;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.concurrent.atomic.AtomicInteger;

// 客户端重连计数器
public class RetryCounter {
    private final static TransmittableThreadLocal<Integer> threadLocal = new TransmittableThreadLocal<>();
    private final static AtomicInteger counter = new AtomicInteger();

    public static void increment(){
        threadLocal.set(counter.incrementAndGet());
    }

    public static Integer get(){
        return counter.get();
    }
}
