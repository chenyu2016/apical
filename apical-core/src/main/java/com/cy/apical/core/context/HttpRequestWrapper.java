package com.cy.apical.core.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author ChenYu
 * @Date 2022/3/19 下午10:52
 * @Describe http请求包装类
 * @Version 1.0
 */
@Getter
@Setter
public class HttpRequestWrapper implements RequestWapper{

    private FullHttpRequest fullHttpRequest;
    private ChannelHandlerContext ctx;
}
