package com.cy.apical.core.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @Author ChenYu
 * @Date 2022/6/26 下午6:43
 * @Describe 基础上下文实现类
 * @Version 1.0
 */
public abstract class BasicContext implements Context{

    protected final String protocol;

    protected final ChannelHandlerContext nettyCtx;

    protected final boolean keepAlive;

    /** 当前上下文的状态标示 默认状态RUNNING */
    protected volatile int status = Context.RUNNING;

    /** 保存所有的 上下文参数 */
    protected final Map<AttributeKey<?>, Object> attributes = new ConcurrentHashMap<>();

    /** 请求过程中 发生异常 设置 */
    protected Throwable throwable;

    /** 定义是否已经释放请求资源 */
    protected final AtomicBoolean requestReleased = new AtomicBoolean(false);

    /**	存放回调函数的集合 */
    protected List<Consumer<Context>> completedCallbacks;

    public BasicContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        this.protocol = protocol;
        this.nettyCtx = nettyCtx;
        this.keepAlive = keepAlive;
    }

    @Override
    public ChannelHandlerContext getNettyCtx() {
        return nettyCtx;
    }

    @Override
    public boolean isKeepAlive() {
        return keepAlive;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public void setRunning() {
        status = Context.RUNNING;
    }

    @Override
    public void setWritten() {
        status = Context.WRITTEN;
    }

    @Override
    public void setCompleted() {
        status = Context.COMPLETED;
    }

    @Override
    public void setTerminated() {
        status = Context.TERMINATED;
    }

    @Override
    public boolean isRunning() {
        return status == Context.RUNNING;
    }

    @Override
    public boolean isWrittened() {
        return status == Context.WRITTEN;
    }

    @Override
    public boolean isCompleted() {
        return status == Context.COMPLETED;
    }

    @Override
    public boolean isTerminated() {
        return status == Context.TERMINATED;
    }


    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(AttributeKey<T> key) {
        return (T) attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T putAttribute(AttributeKey<T> key, T value) {
        return (T) attributes.put(key,value);
    }

    @Override
    public void releaseRequest() {
        this.requestReleased.compareAndSet(false,true);
    }

    @Override
    public void completedCallback(Consumer<Context> consumer) {
        if(null == completedCallbacks){
            completedCallbacks = new ArrayList<>();
        }
        completedCallbacks.add(consumer);
    }

    @Override
    public void invokeCompletedCallback() {
        if(null == completedCallbacks){
            return ;
        }
        completedCallbacks.forEach(call -> call.accept(this));
    }
    
}
