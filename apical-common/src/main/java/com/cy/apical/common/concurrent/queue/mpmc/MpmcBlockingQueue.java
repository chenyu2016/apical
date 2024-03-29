package com.cy.apical.common.concurrent.queue.mpmc;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author ChenYu
 * @Date 2022/6/5 下午4:41
 * @Describe 多生产者多消费者阻塞队列
 * @Version 1.0
 */
public class MpmcBlockingQueue<E> extends MpmcConcurrentQueue<E> implements Serializable, Iterable<E>, Collection<E>, BlockingQueue<E>, Queue<E>, ConcurrentQueue<E>{

    /** 如果是满的就阻塞*/
    protected final Condition queueNotFullCondition;
    /** 如果是空的就阻塞*/
    protected final Condition queueNotEmptyCondition;

    public MpmcBlockingQueue(int capacity) {
        this(capacity,SpinPolicy.WAITING);
    }

    public MpmcBlockingQueue(int capacity,SpinPolicy spinPolicy) {
        super(capacity);
        switch(spinPolicy) {
            case BLOCKING:
            case SPINNING:
            case WAITING:
            default:
                queueNotFullCondition = new QueueNotFull();
                queueNotEmptyCondition = new QueueNotEmpty();
                break;
        }
    }

    @Override
    public void put(E e) throws InterruptedException {
        // 放不进去
        while (!offer(e)) {
            if(Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            // 就等待
            queueNotFullCondition.await();
        }
    }

    @Override
    public final boolean offer(E e) {
        if (super.offer(e)) {
            queueNotEmptyCondition.signal();
            return true;
        } else {
            queueNotEmptyCondition.signal();
            return false;
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        for (;;) {
            if (offer(e)) {
                return true;
            } else {
                if (!Condition.waitStatus(timeout, unit, queueNotFullCondition)) return false;
            }
        }
    }

    @Override
    public E take() throws InterruptedException {
        for (;;) {
            E pollObj = poll();
            if (null != pollObj) {
                return pollObj;
            }
            if(Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            queueNotEmptyCondition.await();
        }
    }

    @Override
    public final E poll() {
        final E e = super.poll();
        queueNotFullCondition.signal();
        return e;
    }

    @Override
    public int remove(final E[] e) {
        final int n = super.remove(e);
        queueNotFullCondition.signal();
        return n;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        for(;;) {
            E pollObj = poll();
            if(pollObj != null) {
                return pollObj;
            } else {
                // 等待超时时间
                if(!Condition.waitStatus(timeout, unit, queueNotEmptyCondition)) return null;
            }
        }
    }

    @Override
    public int remainingCapacity() {
        return size - size();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, size());
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        // required by spec
        if (this == c) throw new IllegalArgumentException("Can not drain to self.");

        // batch remove is not supported in MPMC
        int nRead = 0;

        while(!isEmpty() && maxElements > 0) {
            final E e = poll();
            if(e != null) {
                c.add(e);
                nRead++;
            }
        }
        // only return the number that was actually added to the collection
        return nRead;
    }

    @Override
    public E remove() {
        return poll();
    }

    @Override
    public E element() {
        final E val = peek();
        if (val != null)
            return val;
        throw new NoSuchElementException("No element found.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] toArray() {
        final E[] e = (E[]) new Object[size()];
        toArray(e);
        return e;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        remove((E[]) a);
        return a;
    }

    @Override
    public boolean add(E e) {
        if (offer(e)) return true;
        throw new IllegalStateException("queue is full");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (final Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (final E e : c) {
            if (!offer(e)) return false;
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return new RingIter();
    }

    /**
     * 是否满  尾部-头部==队列大小
     * @return
     */
    private final boolean isFull() {
        return tail.get() - head.get() == size;
    }

    private final class RingIter implements Iterator<E> {
        int dx = 0;

        E lastObj = null;

        private RingIter() {
        }

        @Override
        public boolean hasNext() {
            return dx < size();
        }

        @Override
        public E next() {
            final long pollPos = head.get();
            final int slot = (int) ((pollPos + dx++) & mask);
            lastObj = buffer[slot].entry;
            return lastObj;
        }

        @Override
        public void remove() {
            MpmcBlockingQueue.this.remove(lastObj);
        }
    }

    /**
     * 队列是否满的条件
     */
    private final class QueueNotFull extends ConditionAbstract {
        @Override
        public boolean test() {
            return isFull();
        }
    }

    /**
     *
     */
    private final class QueueNotEmpty extends ConditionAbstract {
        @Override
        public boolean test() {
            return isEmpty();
        }
    }
}
