package com.cy.apical.disruptor.quick;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * @Author ChenYu
 * @Date 2022/5/1 下午7:31
 * @Describe 生产者对象
 * @Version 1.0
 */
public class OrderEventProducer {

    private RingBuffer<OrderEvent>ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void putData(ByteBuffer buffer){
        // 取可用序号
        long sequence = ringBuffer.next();
        try {
            // 取元素 设置值
            OrderEvent event = ringBuffer.get(sequence);
            event.setValue(buffer.getLong(0));
        } finally {
            // 发布
            ringBuffer.publish(sequence);
        }

    }
}
