package com.cy.apical.core.netty.handler;

import com.cy.apical.core.ApicalConfig;
import com.cy.apical.core.processor.NettyProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.*;

import java.util.List;

/**
 * @Author ChenYu
 * @Date 2022/4/10 上午9:11
 * @Describe 端口统一处理 端口复用
 * @Version 1.0
 */
public class PortUnificationServerHandler extends ByteToMessageDecoder {

    private NettyProcessor nettyProcessor;

    private final ApicalConfig config;

    public PortUnificationServerHandler(NettyProcessor nettyProcessor, ApicalConfig config) {
        this.nettyProcessor = nettyProcessor;
        this.config = config;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 至少需要前5个字节来检查协议
        if (in.readableBytes() < 5) {
            return;
        }

        final int magic1 = in.getUnsignedByte(in.readerIndex());
        final int magic2 = in.getUnsignedByte(in.readerIndex() + 1);
        if (isHttp(magic1, magic2)) {
            switchToHttp(ctx);
        } else {
            switchToSocket(ctx);
        }
    }

    private static boolean isHttp(int magic1, int magic2) {
        return
                magic1 == 'G' && magic2 == 'E' || // GET
                        magic1 == 'P' && magic2 == 'O' || // POST
                        magic1 == 'P' && magic2 == 'U' || // PUT
                        magic1 == 'H' && magic2 == 'E' || // HEAD
                        magic1 == 'O' && magic2 == 'P' || // OPTIONS
                        magic1 == 'P' && magic2 == 'A' || // PATCH
                        magic1 == 'D' && magic2 == 'E' || // DELETE
                        magic1 == 'T' && magic2 == 'R' || // TRACE
                        magic1 == 'C' && magic2 == 'O';   // CONNECT
    }

    /**
     * 跳转到http
     * @param ctx
     */
    private void switchToHttp(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(config.getMaxContentLength()));
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new NettyServerConnectManagerHandler());
        p.addLast(new NettyHttpServerHandler(nettyProcessor));
        p.remove(this);
    }

    /**
     * 跳转到Socket
     * @param ctx
     */
    private void switchToSocket(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
//        p.addLast("decoder", new HttpRequestDecoder());
//        p.addLast("encoder", new HttpRequestDecoder());
//        p.addLast("deflater", new HttpContentCompressor());
        //p.addLast("handler", new HttpSnoopServerHandler());
        p.remove(this);
    }
}
