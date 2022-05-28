package com.cy.apical.disruptor.quick;

/**
 * @Author ChenYu
 * @Date 2022/5/1 下午7:13
 * @Describe
 * @Version 1.0
 */
public class OrderEvent {

    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
