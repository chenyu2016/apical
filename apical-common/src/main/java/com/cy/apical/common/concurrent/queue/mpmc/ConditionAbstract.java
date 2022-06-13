package com.cy.apical.common.concurrent.queue.mpmc;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author ChenYu
 * @Date 2022/6/5 下午4:54
 * @Describe
 * @Version 1.0
 */
public abstract class ConditionAbstract implements Condition{

    private final ReentrantLock queueLock = new ReentrantLock();

    private final java.util.concurrent.locks.Condition condition = queueLock.newCondition();

    @Override
    public boolean test() {
        return false;
    }

    @Override
    public void awaitNanos(long timeout) throws InterruptedException {
        long remaining = timeout;
        queueLock.lock();
        try {
            while (test() && remaining > 0) {
                remaining = condition.awaitNanos(remaining);
            }
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void await() throws InterruptedException {
        queueLock.lock();
        try {
            while(test()) {
                condition.await();
            }
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void signal() {
        queueLock.lock();
        try {
            condition.signalAll();
        }
        finally {
            queueLock.unlock();
        }
    }
}
