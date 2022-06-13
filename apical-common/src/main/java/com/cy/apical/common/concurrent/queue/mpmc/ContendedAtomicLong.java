package com.cy.apical.common.concurrent.queue.mpmc;

import java.util.concurrent.atomic.AtomicLongArray;

import static com.cy.apical.common.concurrent.queue.mpmc.Contended.CACHE_LINE;

/**
 * @Author ChenYu
 * @Date 2022/6/4 下午11:36
 * @Describe by jctools  主要是为了消除伪共享
 * @Version 1.0
 */
public class ContendedAtomicLong {

    /** 一个缓存行需要多少个Long元素的填充：8 */
    private static final int CACHE_LINE_LONGS = CACHE_LINE / Long.BYTES;

    private final AtomicLongArray contendedArray;

    ContendedAtomicLong(final long init) {
        // 两个缓存行 保证了 设置的值 肯定 独享一个缓存行
        contendedArray = new AtomicLongArray(2 * CACHE_LINE_LONGS);
        set(init);
    }

    void set(final long l) {
        contendedArray.set(CACHE_LINE_LONGS, l);
    }

    long get() {
        return contendedArray.get(CACHE_LINE_LONGS);
    }

    public String toString() {
        return Long.toString(get());
    }

    public boolean compareAndSet(final long expect, final long l) {
        return contendedArray.compareAndSet(CACHE_LINE_LONGS, expect, l);
    }
}
