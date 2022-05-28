package com.cy.apical.core;

import com.cy.apical.common.constants.ApicalBufferHelper;
import com.cy.apical.common.constants.BasicConst;
import com.cy.apical.common.util.NetUtils;
import lombok.Data;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午7:45
 * @Describe 网关的通用配置信息
 * @Version 1.0
 */
@Data
public class ApicalConfig {

    /** 端口号 */
    private int port=8888;

    /** 网关唯一ID*/
    private String apicalId = NetUtils.getLocalIp() + BasicConst.COLON_SEPARATOR + port;

    /** 网关注册中心地址*/
    private String registerAddress = "http://192.168.1.120:2379,http://192.168.1.121:2379,http://192.168.1.122:2379";

    /** 网关命名空间*/
    private String nameSpace = "dev";

    /** 网关CPU映射的线程数*/
    private int processThread = Runtime.getRuntime().availableProcessors();

    /** netty的boss线程数*/
    private int eventLoopGroupBossNum = 1;

    /** netty的work线程数*/
    private int eventLoopGroupWorkNum = processThread;

    /** 是否开启epoll*/
    private boolean useEPoll = true;

    /** 是否开启netty内存分配机制*/
    private boolean nettyAllocator = true;

    /** 消息最大长度*/
    private int maxContentLength = 64 * 1024 * 1024;

    /** dubbo开启链接数*/
    private int dubboConnections = processThread;

    /**
     * 单异步模式
     * completableFuture whenComplete whenCompleteAsync
     */
    private boolean whenComplete = true;

    /** 网关队列缓冲模式*/
    private String bufferType = ApicalBufferHelper.MPMC;

    /** 网关缓冲队列大小*/
    private int bufferSize = 1024 * 16;

    /** 网关队列 等待策略*/
    private String waitStrategy = "blocking";

    /** 连接超时时间 */
    private int httpConnectTimeout = 30 * 1000;

    /** 请求超时时间 */
    private int httpRequestTimeout = 30 * 1000;

    /** 客户端请求重试次数 */
    private int httpMaxRequestRetry = 2;

    /** 客户端请求最大连接数 */
    private int httpMaxConnections = 10000;

    /** 客户端每个地址支持的最大连接数 */
    private int httpConnectionsPerHost = 8000;

    /** 客户端空闲连接超时时间, 默认60秒 */
    private int httpPooledConnectionIdleTimeout = 60 * 1000;
}
