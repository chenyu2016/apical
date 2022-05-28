package com.cy.apical.disruptor.multi;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * @Author ChenYu
 * @Date 2022/5/3 上午11:39
 * @Describe 多生产多消费
 * @Version 1.0
 */
public class DisruptorMain {

    public static void main(String[] args) {
        // 1.创建 ringbuffer
        RingBuffer<OrderEvent> ringBuffer = RingBuffer.create(ProducerType.MULTI,
                new EventFactory<OrderEvent>() {
                    @Override
                    public OrderEvent newInstance() {
                        return new OrderEvent();
                    }
                },1024*1024,new BlockingWaitStrategy());
        // 设置屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        // 创建多消费者数组
        ConsumerWorkHandler[] consumerWorkHandlers = new ConsumerWorkHandler[10];
        for(int i=0;i<consumerWorkHandlers.length;i++){
            consumerWorkHandlers[i] = new ConsumerWorkHandler("Consumer:"+i);
        }
        // 构造多消费者工作池
        WorkerPool<OrderEvent> workerPool = new WorkerPool<>(ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                consumerWorkHandlers);

        // 设置多消费序号
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        // 启动 workerpool
        workerPool.start(Executors.newFixedThreadPool(10));

        
    }
}
