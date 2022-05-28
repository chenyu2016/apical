package com.cy.apical.disruptor.multi;

import com.lmax.disruptor.ExceptionHandler;

/**
 * @Author ChenYu
 * @Date 2022/5/3 下午3:12
 * @Describe
 * @Version 1.0
 */
public class EventExceptionHandler implements ExceptionHandler<OrderEvent> {

    @Override
    public void handleEventException(Throwable ex, long sequence, OrderEvent event) {

    }

    @Override
    public void handleOnStartException(Throwable ex) {

    }

    @Override
    public void handleOnShutdownException(Throwable ex) {

    }
}
