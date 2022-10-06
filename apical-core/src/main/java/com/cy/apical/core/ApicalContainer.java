package com.cy.apical.core;

import com.cy.apical.common.constants.ApicalBufferHelper;
import com.cy.apical.core.netty.client.NettyHttpClient;
import com.cy.apical.core.netty.server.NettyServer;
import com.cy.apical.core.processor.NettyDisruptorProcessor;
import com.cy.apical.core.processor.NettyCoreProcessor;
import com.cy.apical.core.processor.NettyMpscProcessor;
import com.cy.apical.core.processor.NettyProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午11:59
 * @Describe 主流程容器类
 * @Version 1.0
 */
@Slf4j
public class ApicalContainer implements LifeCycle{

    /** 核心配置 */
    private ApicalConfig apicalConfig;

    /** 核心server */
    private NettyServer nettyServer;

    /** 核心处理 */
    private NettyProcessor nettyProcessor;

    /** http转发的核心类 */
    private NettyHttpClient nettyHttpClient;

    public ApicalContainer(ApicalConfig apicalConfig) {
        this.apicalConfig = apicalConfig;
        init();
    }

    @Override
    public void init() {
        // 构造 nettyProcessor
        NettyCoreProcessor nettyCoreProcessor = new NettyCoreProcessor();
        // 确定 缓冲区类型
        String buffType = this.apicalConfig.getBufferType();

        if(ApicalBufferHelper.isDisruptor(buffType)){
            nettyProcessor = new NettyDisruptorProcessor(nettyCoreProcessor,apicalConfig);
        } else if(ApicalBufferHelper.isMpmc(buffType)){
            nettyProcessor = new NettyMpscProcessor(nettyCoreProcessor,apicalConfig,true);
        } else {
            nettyProcessor = nettyCoreProcessor;
        }
        // 创建nettyServer
        nettyServer = new NettyServer(apicalConfig, nettyProcessor);
        // 创建nettyHttpClient
        nettyHttpClient = new NettyHttpClient(apicalConfig, nettyServer.getEventLoopGroupWork());
    }

    @Override
    public void start() {
        nettyProcessor.start();
        nettyServer.start();
        //nettyHttpClient.start();
        log.info("ApicalContainer started !");
    }

    @Override
    public void shutdown() {
        nettyProcessor.shutdown();
        nettyServer.shutdown();
    }
}
