package com.cy.apical.core.netty.client;

import com.cy.apical.core.ApicalConfig;
import com.cy.apical.core.LifeCycle;
import com.cy.apical.core.helper.AsyncHttpHelper;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

/**
 * @Author ChenYu
 * @Date 2022/4/17 下午9:41
 * @Describe HTTP客户端启动类
 * @Version 1.0
 */
@Slf4j
public class NettyHttpClient implements LifeCycle {

    private AsyncHttpClient asyncHttpClient;

    private DefaultAsyncHttpClientConfig.Builder clientBuilder;

    private ApicalConfig config;

    private EventLoopGroup workGroup;

    public NettyHttpClient(ApicalConfig config,EventLoopGroup workGroup) {
        this.config = config;
        this.workGroup = workGroup;
        init();
    }

    @Override
    public void init() {
        clientBuilder = new DefaultAsyncHttpClientConfig.Builder()
                .setFollowRedirect(false)
                .setEventLoopGroup(workGroup)
                .setConnectTimeout(config.getHttpConnectTimeout())
                .setRequestTimeout(config.getHttpRequestTimeout())
                .setMaxRequestRetry(config.getHttpMaxRequestRetry())
                .setAllocator(PooledByteBufAllocator.DEFAULT)
                .setCompressionEnforced(true)
                .setMaxConnections(config.getHttpMaxConnections())
                .setMaxConnectionsPerHost(config.getHttpConnectionsPerHost())
                .setPooledConnectionIdleTimeout(config.getHttpPooledConnectionIdleTimeout());
    }

    @Override
    public void start() {
        this.asyncHttpClient = new DefaultAsyncHttpClient(clientBuilder.build());
        AsyncHttpHelper.getInstance().initialized(asyncHttpClient);
    }

    @Override
    public void shutdown() {
        if(asyncHttpClient != null) {
            try {
                this.asyncHttpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("NettyHttpClient.shutdown shutdown error", e);
            }
        }
    }
}
