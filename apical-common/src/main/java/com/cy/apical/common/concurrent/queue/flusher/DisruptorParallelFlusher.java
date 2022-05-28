package com.cy.apical.common.concurrent.queue.flusher;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * @Author ChenYu
 * @Date 2022/5/2 下午8:42
 * @Describe 多生产多消费
 * @Version 1.0
 */
public class DisruptorParallelFlusher<T> implements Flusher<T>{

    @Override
    public void add(T e) {

    }

    @Override
    public boolean tryAdd(T e) {
        return false;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }


    /**
     * 建造者模式
     * @param <T>
     */
    public static class Builder<T>{
        private ProducerType producerType = ProducerType.MULTI;

        private int bufferSize = 1024 * 1024;

        private int threads = 1;

        private String namePrefix = "";

        private WaitStrategy waitStrategy = new BlockingWaitStrategy();
    }
}
