package com.cy.apical.core.netty.handler;

import com.cy.apical.core.context.HttpRequestWrapper;
import com.cy.apical.core.processor.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author ChenYu
 * @Date 2022/3/19 下午9:41
 * @Describe
 * @Version 1.0
 */
@Slf4j
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {

    /** 核心执行器 */
    private NettyProcessor nettyProcessor;

    public NettyHttpServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof HttpRequest) {
            FullHttpRequest request = (FullHttpRequest)msg;
            HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper();
            httpRequestWrapper.setFullHttpRequest(request);
            httpRequestWrapper.setCtx(ctx);

            //	processor
            nettyProcessor.process(httpRequestWrapper);

        } else {
            log.error("NettyHttpServerHandler.channelRead# message type is not httpRequest: {}", msg);
            boolean release = ReferenceCountUtil.release(msg);
            if(!release) {
                log.error("NettyHttpServerHandler.channelRead# release fail 资源释放失败");
            }
        }
    }
}
