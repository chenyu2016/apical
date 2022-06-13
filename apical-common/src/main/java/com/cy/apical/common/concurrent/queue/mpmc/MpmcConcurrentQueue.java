package com.cy.apical.common.concurrent.queue.mpmc;

/**
 * @Author ChenYu
 * @Date 2022/6/4 下午11:50
 * @Describe 多生产者 多消费者 并发队列 by jctools
 * @Version 1.0
 */
public class MpmcConcurrentQueue<E> implements ConcurrentQueue<E> {

    protected final int size;

    // 掩码
    final long mask;

    // 类似环形数组
    final Cell<E>[] buffer;

    // 头部计数器
    final ContendedAtomicLong head = new ContendedAtomicLong(0L);

    // 尾部计数器
    final ContendedAtomicLong tail = new ContendedAtomicLong(0L);

    @SuppressWarnings("unchecked")
    public MpmcConcurrentQueue(int capacity) {
        int c = 2;
        // 2的整数倍
        while (c<capacity){
            c <<= 1;
        }
        size = c;
        mask = size-1L;
        buffer = new Cell[size];
        for(int i=0;i<size;i++){
            buffer[i] = new Cell<>(i);
        }
    }

    @Override
    public boolean offer(E e) {
        Cell<E> cell;
        long tail = this.tail.get();
        for(;;){
            // 取得当前在数组里的位置
            cell = buffer[(int)(tail & mask)];
            long dif = cell.seq.get() - tail;
            if(dif == 0){
                if(this.tail.compareAndSet(tail,tail+1)){
                    break;
                }
            } else if(dif<0){
                return false;
            } else {
                tail = this.tail.get();
            }
        }
        cell.entry = e;
        cell.seq.set(tail+1);
        return true;
    }

    @Override
    public E poll() {
        Cell<E> cell;
        long head = this.head.get();
        for(;;){
            cell = buffer[(int)(head & mask)];
            long seq = cell.seq.get();
            long dif = seq - head - 1L;
            if(dif == 0){
                if(this.head.compareAndSet(head,head+1L)){
                    break;
                }
            } else if(dif < 0){
                return null;
            } else {
                head = this.head.get();
            }
        }

        try{
            return cell.entry;
        } finally {
            // 值置空
            cell.entry = null;
            // 要绕一圈
            cell.seq.set(head+ mask +1L);
        }
    }

    @Override
    public E peek() {
        return buffer[(int)(head.get() & mask)].entry;
    }

    @Override
    public int size() {
        return (int)Math.max((tail.get() - head.get()), 0);
    }

    @Override
    public int capacity() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        // 头尾相同 没有数据
        return head.get() == tail.get();
    }

    @Override
    public boolean contains(Object o) {
        for(int i=0; i<size(); i++) {
            final int slot = (int)((head.get() + i) & mask);
            if(buffer[slot].entry != null && buffer[slot].entry.equals(o)) return true;
        }
        return false;
    }

    @Override
    public int remove(E[] e) {
        int nRead = 0;
        while(nRead < e.length && !isEmpty()) {
            final E entry = poll();
            if(entry != null) {
                e[nRead++] = entry;
            }
        }
        return nRead;
    }

    @Override
    public void clear() {
        while(!isEmpty()) poll();
    }


    /**
     * 消除伪共享对象
     * @param <R>
     */
    protected static final class Cell<R> {

        //	计数器
        final ContendedAtomicLong seq = new ContendedAtomicLong(0L);

        //	实际的内容
        R entry;

        Cell(final long s) {
            seq.set(s);
            entry = null;
        }

    }
}
