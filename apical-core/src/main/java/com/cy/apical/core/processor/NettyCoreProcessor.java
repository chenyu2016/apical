package com.cy.apical.core.processor;

import com.cy.apical.core.context.HttpRequestWrapper;
import com.cy.apical.core.context.RequestWapper;
import com.cy.apical.core.context.SocketRequestWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @Author ChenYu
 * @Date 2022/3/19 下午11:11
 * @Describe 核心执行逻辑
 * @Version 1.0
 */
public class NettyCoreProcessor implements NettyProcessor{


    private void processHttp(HttpRequestWrapper httpRequestWrapper) throws Exception {
        FullHttpRequest request = httpRequestWrapper.getFullHttpRequest();
        ChannelHandlerContext ctx = httpRequestWrapper.getCtx();
        try{
            // 解析 FullHttpRequest 转换为 context
            // 执行整个过滤器逻辑 FilterChain
            System.out.println("接受到 http 请求");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void processSocket(SocketRequestWrapper socketRequestWrapper) throws Exception {
        try{
            System.out.println("接受到 socket 请求");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void process(RequestWapper requestWrapper) throws Exception {
        if(requestWrapper instanceof HttpRequestWrapper){
            processHttp((HttpRequestWrapper)requestWrapper);
        } else if(requestWrapper instanceof SocketRequestWrapper){
            processSocket((SocketRequestWrapper)requestWrapper);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
