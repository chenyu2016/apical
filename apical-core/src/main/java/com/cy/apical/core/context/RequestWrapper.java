package com.cy.apical.core.context;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author ChenYu
 * @Date 2022/3/19 下午10:55
 * @Describe 请求包装类接口
 * @Version 1.0
 */
public abstract class RequestWrapper {

    ChannelHandlerContext ctx = null;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * 处理出错的会写
     * @param o
     */
    public abstract void errorCallBackWrite(Object o);
}
