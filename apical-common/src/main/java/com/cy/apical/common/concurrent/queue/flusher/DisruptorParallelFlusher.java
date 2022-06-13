package com.cy.apical.common.concurrent.queue.flusher;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author ChenYu
 * @Date 2022/5/2 下午8:42
 * @Describe 多生产多消费
 * @Version 1.0
 */
public class DisruptorParallelFlusher<T> implements Flusher<T>{

    private RingBuffer<HolderEvent> ringBuffer;

    private EventListener<T> eventListener;

    private WorkerPool<HolderEvent> workerPool;

    private ExecutorService executorService;

    private EventTranslatorOneArg<HolderEvent,T> eventTranslator;

    public DisruptorParallelFlusher(Builder<T> builder) {

        this.executorService = Executors.newFixedThreadPool(builder.threads
                , new ThreadFactoryBuilder().setNameFormat("Flusher-"+builder.namePrefix+"-pool-%d").build());

        this.eventListener = builder.eventListener;
        this.eventTranslator = new HolderEventTranslator();

        // 创建 ringbuffer
        ringBuffer = RingBuffer.create(builder.producerType
                , new HolderEventFactory()
                , builder.bufferSize
                , builder.waitStrategy);

        // 创建 sequence
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        // 创建 多消费者数组
        WorkHandler<HolderEvent>[] workHandlers = new WorkHandler[builder.threads];
        for(int i=0;i<workHandlers.length;i++){
            workHandlers[i] = new HolderWorkHandler();
        }

        // 创建 多消费者工作池
        workerPool = new WorkerPool<>(ringBuffer
                ,sequenceBarrier
                ,new HolderExceptionHandler()
                ,workHandlers);

        // 设置 多消费者序号
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());


    }

    private static <E> void process(EventListener<E> listener,Throwable throwable, E event){
        listener.onException(throwable,-1,event);
    }
    private static <E> void process(EventListener<E> listener,Throwable throwable, E... events){
        for(E e:events) {
            listener.onException(throwable, -1, e);
        }
    }

    @Override
    public void add(T e) {
        if(null == ringBuffer){
            process(this.eventListener,new IllegalStateException("close"),e);
            return;
        }
        try {
            ringBuffer.publishEvent(eventTranslator, e);
        } catch (Throwable throwable){
            process(this.eventListener,new IllegalStateException("close"),e);
        }
    }

    @Override
    public void add(T... es) {
        if(null == ringBuffer){
            process(this.eventListener,new IllegalStateException("close"),es);
            return;
        }
        try {
            ringBuffer.publishEvents(eventTranslator, es);
        } catch (Throwable throwable){
            process(this.eventListener,new IllegalStateException("close"),es);
        }
    }

    @Override
    public boolean tryAdd(T e) {
        if(null == ringBuffer){
            return false;
        }
        try {
            return ringBuffer.tryPublishEvent(eventTranslator, e);
        } catch (Throwable throwable){
            return false;
        }
    }

    @Override
    public boolean tryAdd(T... es) {
        if(null == ringBuffer){
            return false;
        }
        try {
            return ringBuffer.tryPublishEvents(eventTranslator, es);
        } catch (Throwable throwable){
            return false;
        }
    }

    @Override
    public boolean isShutdown() {
        return null == ringBuffer;
    }

    @Override
    public void start() {
        // 启动 workerpool
        this.workerPool.start(executorService);
    }

    @Override
    public void shutdown() {
        RingBuffer<HolderEvent> tmp = ringBuffer;
        if(null == tmp)
            return;
        if(null != workerPool)
            workerPool.drainAndHalt();
        if(null != executorService)
            executorService.shutdown();
    }


    /**
     * 建造者模式
     * @param <T>
     */
    public static class Builder<T>{
        private ProducerType producerType = ProducerType.MULTI;

        private int bufferSize = 1024 * 1024;

        private int threads = 1;

        private String namePrefix = "";

        private WaitStrategy waitStrategy = new BlockingWaitStrategy();

        private EventListener<T> eventListener;

        public Builder<T> setProducerType(ProducerType producerType) {
            this.producerType = producerType;
            return this;
        }

        public Builder<T> setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder<T> setThreads(int threads) {
            Preconditions.checkArgument(Integer.bitCount(bufferSize)==1);
            this.threads = threads;
            return this;
        }

        public Builder<T> setNamePrefix(String namePrefix) {
            Preconditions.checkNotNull(namePrefix);
            this.namePrefix = namePrefix;
            return this;
        }

        public Builder<T> setWaitStrategy(WaitStrategy waitStrategy) {
            this.waitStrategy = waitStrategy;
            return this;
        }

        public Builder<T> setEventListener(EventListener<T> eventListener) {
            this.eventListener = eventListener;
            return this;
        }

        public DisruptorParallelFlusher<T> buid(){
            return new DisruptorParallelFlusher<>(this);
        }
    }

    private class HolderEvent{
        private T e;

        public void setE(T e) {
            this.e = e;
        }
    }

    private class HolderEventFactory implements EventFactory<HolderEvent>{

        @Override
        public HolderEvent newInstance() {
            return new HolderEvent();
        }
    }

    public interface EventListener<E> {
        void onEvent(E e) throws Exception;

        void onException(Throwable throwable, long sequence, E event);
    }

    private class HolderWorkHandler implements WorkHandler<HolderEvent>{

        @Override
        public void onEvent(HolderEvent event) throws Exception {
            eventListener.onEvent(event.e);
            event.setE(null);
        }
    }

    private class HolderExceptionHandler implements ExceptionHandler<HolderEvent> {

        @Override
        public void handleEventException(Throwable ex, long sequence, HolderEvent event) {
            try{
                eventListener.onException(ex,sequence, event.e);
            } catch (Exception e){

            } finally {
                event.setE(null);
            }
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    private class HolderEventTranslator implements EventTranslatorOneArg<HolderEvent, T>{

        @Override
        public void translateTo(HolderEvent event, long sequence, T arg0) {
            event.setE(arg0);
        }
    }
}
