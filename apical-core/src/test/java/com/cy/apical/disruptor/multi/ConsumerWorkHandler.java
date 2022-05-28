package com.cy.apical.disruptor.multi;

import com.lmax.disruptor.WorkHandler;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author ChenYu
 * @Date 2022/5/3 下午2:02
 * @Describe 消费者
 * @Version 1.0
 */
public class ConsumerWorkHandler implements WorkHandler<OrderEvent> {

    private String comsumerId;

    private static AtomicInteger count = new AtomicInteger();

    private Random random = new Random();

    public ConsumerWorkHandler(String comsumerId) {
        this.comsumerId = comsumerId;
    }

    @Override
    public void onEvent(OrderEvent event) throws Exception {
        Thread.sleep(1 * random.nextInt(5));
        System.err.println("当前消费者 " + this.comsumerId +" 消息："+ event.getId());
        count.incrementAndGet();
    }

    public int getCount(){
        return count.get();
    }
}
