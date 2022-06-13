package com.cy.apical.common.concurrent.queue.mpmc;

/**
 * @Author ChenYu
 * @Date 2022/6/4 下午11:26
 * @Describe
 * @Version 1.0
 */
public interface ConcurrentQueue<E> {
    boolean offer(E e);

    E poll();

    E peek();

    int size();

    int capacity();

    boolean isEmpty();

    boolean contains(Object o);

    int remove(E[] e);

    void clear();
}
