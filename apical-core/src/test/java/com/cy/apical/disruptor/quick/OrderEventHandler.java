package com.cy.apical.disruptor.quick;

import com.lmax.disruptor.EventHandler;
import org.apache.commons.lang3.RandomUtils;

/**
 * @Author ChenYu
 * @Date 2022/5/1 下午7:20
 * @Describe
 * @Version 1.0
 */
public class OrderEventHandler implements EventHandler<OrderEvent> {


    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        // 随机休眠
        Thread.sleep(RandomUtils.nextInt(1,100));
        System.err.println("消费者消费：" + event.getValue());
    }
}
