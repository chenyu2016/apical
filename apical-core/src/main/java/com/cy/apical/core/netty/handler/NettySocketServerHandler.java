package com.cy.apical.core.netty.handler;

import com.cy.apical.core.context.HttpRequestWrapper;
import com.cy.apical.core.context.SocketRequestWrapper;
import com.cy.apical.core.processor.NettyProcessor;
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
public class NettySocketServerHandler extends ChannelInboundHandlerAdapter {
    /** 核心执行器 */
    private NettyProcessor nettyProcessor;

    public NettySocketServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest) {
            SocketRequestWrapper socketRequestWrapper = new SocketRequestWrapper();
            socketRequestWrapper.setMsg(msg);
            socketRequestWrapper.setCtx(ctx);

            //	processor
            nettyProcessor.process(socketRequestWrapper);

        } else {
            log.error("NettySocketServerHandler.channelRead# message type is not httpRequest: {}", msg);
            boolean release = ReferenceCountUtil.release(msg);
            if(!release) {
                log.error("NettySocketServerHandler.channelRead# release fail 资源释放失败");
            }
        }
    }
}
