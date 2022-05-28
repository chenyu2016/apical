package com.cy.apical.core.netty.server;

import com.cy.apical.common.util.RemotingUtil;
import com.cy.apical.core.ApicalConfig;
import com.cy.apical.core.LifeCycle;
import com.cy.apical.core.netty.handler.PortUnificationServerHandler;
import com.cy.apical.core.processor.NettyProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @Author ChenYu
 * @Date 2022/3/7 上午12:00
 * @Describe 网络服务核心类
 * @Version 1.0
 */
@Slf4j
@Getter
public class NettyServer implements LifeCycle {

    private final ApicalConfig config;

    private int port = 8888;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroupBoss;

    private EventLoopGroup eventLoopGroupWork;

    private NettyProcessor nettyProcessor;

    public NettyServer(ApicalConfig config, NettyProcessor nettyProcessor) {
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        if (config.getPort() > 0 && config.getPort() < 65535) {
            this.port = config.getPort();
        }
        init();
    }

    @Override
    public void init() {
        this.serverBootstrap = new ServerBootstrap();
        if (useEPoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("NettyBossEPoll"));
            this.eventLoopGroupWork = new EpollEventLoopGroup(config.getEventLoopGroupWorkNum(),
                    new DefaultThreadFactory("NettyWorkEPoll"));
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("NettyBossNio"));
            this.eventLoopGroupWork = new NioEventLoopGroup(config.getEventLoopGroupWorkNum(),
                    new DefaultThreadFactory("NettyWorkNio"));
        }
    }

    @Override
    public void start() {
        ServerBootstrap handler = this.serverBootstrap
                .group(eventLoopGroupBoss, eventLoopGroupWork)
                .channel(useEPoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, 65535)
                .childOption(ChannelOption.SO_RCVBUF, 65535)
                .localAddress(new InetSocketAddress(this.port))
                .childHandler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(
                                new PortUnificationServerHandler(nettyProcessor,config)
                        );
                    }
                });

        if (config.isNettyAllocator()) {
            handler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        try {
            this.serverBootstrap.bind().sync();
            log.info("============= apical Server On Port {} =============",port);
        } catch (Exception e) {
            throw new RuntimeException("serverBootstrap.bind() fail", e);
        }
    }

    @Override
    public void shutdown() {
        if (null != eventLoopGroupBoss) {
            eventLoopGroupBoss.shutdownGracefully();
        }

        if (null != eventLoopGroupWork) {
            eventLoopGroupWork.shutdownGracefully();
        }
    }

    /**
     * 是否使用Epoll
     *
     * @return
     */
    public boolean useEPoll() {
        return config.isUseEPoll() && RemotingUtil.isLinuxPlatform() && Epoll.isAvailable();
    }
}
