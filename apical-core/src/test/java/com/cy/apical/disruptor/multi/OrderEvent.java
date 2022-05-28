package com.cy.apical.disruptor.multi;

/**
 * @Author ChenYu
 * @Date 2022/5/3 上午11:41
 * @Describe
 * @Version 1.0
 */
public class OrderEvent {

    private long value;
    private int id;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
