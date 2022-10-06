package com.cy.apical.core.processor;

import com.cy.apical.common.concurrent.queue.mpmc.MpmcBlockingQueue;
import com.cy.apical.core.ApicalConfig;
import com.cy.apical.core.context.RequestWrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author ChenYu
 * @Date 2022/4/5 下午9:44
 * @Describe
 * @Version 1.0
 */
public class NettyMpscProcessor implements NettyProcessor{

    private NettyCoreProcessor nettyCoreProcessor;

    private ApicalConfig config;

    private MpmcBlockingQueue<RequestWrapper> mpmcBlockingQueue;

    private boolean usedPool;
    private ExecutorService executorService;

    private volatile boolean isRunning = false;

    public NettyMpscProcessor(NettyCoreProcessor nettyCoreProcessor,ApicalConfig config,boolean usedPool) {
        this.config = config;
        this.nettyCoreProcessor = nettyCoreProcessor;
        this.mpmcBlockingQueue = new MpmcBlockingQueue<>(this.config.getBufferSize());
        this.usedPool = usedPool;
    }

    @Override
    public void process(RequestWrapper requestWrapper) throws Exception {
        System.out.println("NettyMpscProcessor add ok");
        this.mpmcBlockingQueue.put(requestWrapper);
        System.out.println("NettyMpscProcessor add ok222");
    }

    @Override
    public void start() {
        this.isRunning = true;
        this.nettyCoreProcessor.start();
        int threadSize = 1;
        if(usedPool){
            threadSize = config.getProcessThread();
        }
        this.executorService = Executors.newFixedThreadPool(threadSize);
        for(int i=0;i<threadSize;i++){
            this.executorService.submit(new ConsumerProcessor());
        }
    }

    @Override
    public void shutdown() {
        this.isRunning = false;
        this.nettyCoreProcessor.shutdown();
        this.executorService.shutdown();
    }

    public class ConsumerProcessor implements Runnable {

        @Override
        public void run() {
            while (isRunning){
                RequestWrapper wrapper = null;
                try {
                    wrapper = mpmcBlockingQueue.take();
                    nettyCoreProcessor.process(wrapper);
                } catch (Exception e){
                    e.printStackTrace();
                    if(null != wrapper){

                    } else {

                    }
                }
            }
        }
    }
}
