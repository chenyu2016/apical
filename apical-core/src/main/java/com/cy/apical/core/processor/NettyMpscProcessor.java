package com.cy.apical.core.processor;

import com.cy.apical.core.context.RequestWapper;

/**
 * @Author ChenYu
 * @Date 2022/4/5 下午9:44
 * @Describe
 * @Version 1.0
 */
public class NettyMpscProcessor implements NettyProcessor{

    private NettyCoreProcessor nettyCoreProcessor;

    public NettyMpscProcessor(NettyCoreProcessor nettyCoreProcessor) {
        this.nettyCoreProcessor = nettyCoreProcessor;
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
