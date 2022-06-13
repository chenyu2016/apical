package com.cy.apical.common.concurrent.queue.mpmc;

/**
 * @Author ChenYu
 * @Date 2022/6/11 下午11:16
 * @Describe 自旋策略
 * @Version 1.0
 */
public enum SpinPolicy {
    WAITING,
    BLOCKING,
    SPINNING;
}
