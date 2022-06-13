package com.cy.apical.common.concurrent.queue.mpmc;

/**
 * @Author ChenYu
 * @Date 2022/6/4 下午11:33
 * @Describe Linux 行 缓存大小
 * @Version 1.0
 */
public class Contended {
    public static final int CACHE_LINE = Integer.getInteger("Intel.CacheLineSize", 64);
}
