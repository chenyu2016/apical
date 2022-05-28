package com.cy.apical.disruptor.quick;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;

/**
 * @Author ChenYu
 * @Date 2022/5/1 下午6:37
 * @Describe disruptor 测试类
 * @Version 1.0
 */
public class DisruptorMain {
    public static void main(String[] args) {
        int ringBufferSize = 1024*1024;
        Disruptor<OrderEvent> disruptor = new Disruptor<>(new OrderEventFactory(), ringBufferSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("ds-thread");
                return thread;
            }
        }, ProducerType.SINGLE, new BlockingWaitStrategy());

        disruptor.handleEventsWith(new OrderEventHandler());

        disruptor.start();

        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();

        OrderEventProducer producer = new OrderEventProducer(ringBuffer);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        for(int i=0;i <100;i++) {
            buffer.putLong(0,i);
            producer.putData(buffer);
        }

        disruptor.shutdown();
    }
}
