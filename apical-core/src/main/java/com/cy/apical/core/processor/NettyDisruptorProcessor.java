package com.cy.apical.core.processor;

import com.cy.apical.common.concurrent.queue.flusher.DisruptorParallelFlusher;
import com.cy.apical.core.ApicalConfig;
import com.cy.apical.core.context.RequestWrapper;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author ChenYu
 * @Date 2022/4/5 下午9:45
 * @Describe
 * @Version 1.0
 */
public class NettyDisruptorProcessor implements NettyProcessor{

    private NettyCoreProcessor nettyCoreProcessor;

    private ApicalConfig config;

    private DisruptorParallelFlusher<RequestWrapper> parallelFlusher;

    private static final String THREAD_NAME_PREFIX = "flusher-";

    public NettyDisruptorProcessor(NettyCoreProcessor nettyCoreProcessor, ApicalConfig config) {
        this.nettyCoreProcessor = nettyCoreProcessor;
        this.config = config;
        DisruptorParallelFlusher.Builder<RequestWrapper> builder = new DisruptorParallelFlusher.Builder<>();
        builder.setBufferSize(config.getBufferSize())
                .setThreads(config.getProcessThread())
                .setProducerType(ProducerType.MULTI)
                .setNamePrefix(THREAD_NAME_PREFIX)
                .setWaitStrategy(config.getNewRealWaitStrategy())
                .setEventListener(new MyEventProcessorListener());
        parallelFlusher = builder.buid();
    }

    @Override
    public void process(RequestWrapper requestWrapper) throws Exception {
        parallelFlusher.add(requestWrapper);
    }

    @Override
    public void start() {
        nettyCoreProcessor.start();
        parallelFlusher.start();
    }

    @Override
    public void shutdown() {
        nettyCoreProcessor.shutdown();
        parallelFlusher.shutdown();
    }

    public ApicalConfig getConfig() {
        return config;
    }

    /**
     * 监听数据实际处理
     */
    public class MyEventProcessorListener implements DisruptorParallelFlusher.EventListener<RequestWrapper>{

        @Override
        public void onEvent(RequestWrapper requestWrapper) throws Exception {
            nettyCoreProcessor.process(requestWrapper);
        }

        @Override
        public void onException(Throwable throwable, long sequence, RequestWrapper event) {
            try{
                event.errorCallBackWrite(null);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
