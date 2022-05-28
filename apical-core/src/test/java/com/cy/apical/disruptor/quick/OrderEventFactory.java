package com.cy.apical.disruptor.quick;

import com.lmax.disruptor.EventFactory;

/**
 * @Author ChenYu
 * @Date 2022/5/1 下午7:12
 * @Describe 事件工厂类
 * @Version 1.0
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {

    @Override
    public OrderEvent newInstance() {
        return new OrderEvent();
    }
}
