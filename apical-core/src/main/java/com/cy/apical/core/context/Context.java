package com.cy.apical.core.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.function.Consumer;

/**
 * @Author ChenYu
 * @Date 2022/6/26 下午5:16
 * @Describe 网关上下文接口定义
 * @Version 1.0
 */
public interface Context {

    /** 请求正在执行过程中 */
    int RUNNING = -1;

    /** 写回响应 当前请求需要写回*/
    int WRITTEN = 0;

    /** 写回成功后 标记*/
    int COMPLETED = 1;

    /** 表示整体流程 彻底结束 */
    int TERMINATED = 2;

    void setRunning();
    void setWritten();
    void setCompleted();
    void setTerminated();



    boolean isRunning();
    boolean isWrittened();
    boolean isCompleted();
    boolean isTerminated();

    String getProtocol();

    Object getRule();

    Object getRequest();

    Object getResponse();

    void setResponse(Object response);

    void setThrowable(Throwable throwable);

    Throwable getThrowable();

    <T> T getAttribute(AttributeKey<T> key);

    <T> T putAttribute(AttributeKey<T> key, T value);

    ChannelHandlerContext getNettyCtx();

    boolean isKeepAlive();

    /** 释放请求资源 */
    void releaseRequest();

    /** 设置回调 函数 */
    void completedCallback(Consumer<Context> consumer);

    /** 执行回调 函数 */
    void invokeCompletedCallback();

    /**
     * 	SR(Server Received):	            网关服务器接收到网络请求
     * 	SS(Server Send):		            网关服务器写回请求
     * 	RS(Route Send):						网关客户端发送请求
     * 	RR(Route Received): 				网关客户端收到请求
     */
    long getSRTime();

    void setSRTime(long sRTime);

    long getSSTime();

    void setSSTime(long sSTime);

    long getRSTime();

    void setRSTime(long rSTime);

    long getRRTime();

    void setRRTime(long rRTime);
}
