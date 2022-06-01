package com.cy.apical.common.concurrent.queue.flusher;

/**
 * @Author ChenYu
 * @Date 2022/5/2 下午8:30
 * @Describe
 * @Version 1.0
 */
public interface Flusher<T> {

    /**
     * 添加元素
     * @param e
     */
    void add(T e);

    void add(T... es);

    /**
     * 尝试添加元素
     * @param e
     * @return
     */
    boolean tryAdd(T e);

    boolean tryAdd(T... es);

    boolean isShutdown();

    void start();

    void shutdown();
}
