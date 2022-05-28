package com.cy.apical.core.processor;

import com.cy.apical.core.ApicalConfig;
import com.cy.apical.core.context.RequestWapper;

/**
 * @Author ChenYu
 * @Date 2022/4/5 下午9:45
 * @Describe
 * @Version 1.0
 */
public class NettyBatchEventProcessor implements NettyProcessor{

    private NettyCoreProcessor nettyCoreProcessor;

    private ApicalConfig config;

    public NettyBatchEventProcessor(NettyCoreProcessor nettyCoreProcessor, ApicalConfig config) {
        this.nettyCoreProcessor = nettyCoreProcessor;
        this.config = config;
    }

    @Override
    public void process(RequestWapper requestWrapper) throws Exception {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
