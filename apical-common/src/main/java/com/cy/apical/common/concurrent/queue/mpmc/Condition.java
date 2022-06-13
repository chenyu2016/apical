package com.cy.apical.common.concurrent.queue.mpmc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author ChenYu
 * @Date 2022/6/5 下午4:42
 * @Describe
 * @Version 1.0
 */
public interface Condition {
    long PARK_TIMEOUT = 50L;

    int MAX_PROG_YIELD = 2000;

    /**
     * 检查队列条件 是符合条件的
     * @return
     */
    boolean test();

    /**
     * 带等待时间
     * @param timeout
     * @throws InterruptedException
     */
    void awaitNanos(final long timeout) throws InterruptedException;

    /**
     * 等待
     * @throws InterruptedException
     */
    void await() throws InterruptedException;

    /**
     * 唤醒
     */
    void signal();


    /**
     * 空轮训
     * @param n
     * @return
     */
    static int progressiveYield(final int n) {
        if(n > 500) {
            if(n<1000) {
                // "randomly" yield 1:8
                if((n & 0x7) == 0) {
                    LockSupport.parkNanos(PARK_TIMEOUT);
                } else {
                    onSpinWait();
                }
            } else if(n<MAX_PROG_YIELD) {
                // "randomly" yield 1:4
                if((n & 0x3) == 0) {
                    Thread.yield();
                } else {
                    onSpinWait();
                }
            } else {
                Thread.yield();
                return n;
            }
        } else {
            onSpinWait();
        }
        return n+1;
    }

    /**
     * 空轮训的等待
     */
    static void onSpinWait() {
        // Java 9 hint for spin waiting PAUSE instruction
        //http://openjdk.java.net/jeps/285
        // Thread.onSpinWait();
    }

    /**
     * 等待策略
     * @param timeout
     * @param unit
     * @param condition
     * @return
     * @throws InterruptedException
     */
    static boolean waitStatus(final long timeout, final TimeUnit unit, final Condition condition) throws InterruptedException {
        // until condition is signaled
        final long timeoutNanos = TimeUnit.NANOSECONDS.convert(timeout, unit);
        final long expireTime = System.nanoTime() + timeoutNanos;
        // the queue is empty or full wait for something to change
        // 判断条件
        while (condition.test()) {
            final long now = System.nanoTime();
            if (now > expireTime) {
                return false;
            }
            condition.awaitNanos(expireTime - now - PARK_TIMEOUT);
        }
        return true;
    }
}
