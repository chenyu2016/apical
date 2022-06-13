package com.cy.apical.core.processor;

import com.cy.apical.core.context.RequestWrapper;

/**
 * @Author ChenYu
 * @Date 2022/3/19 下午10:57
 * @Describe 处理Netty核心逻辑的执行器接口定义
 * @Version 1.0
 */
public interface NettyProcessor {

//    /**
//     * 核心执行方法
//     * @param httpRequestWrapper
//     * @throws Exception
//     */
//    void processHttp(HttpRequestWrapper httpRequestWrapper) throws Exception;
//
//    /**
//     * 核心执行方法
//     * @param socketRequestWrapper
//     * @throws Exception
//     */
//    void processSocket(SocketRequestWrapper socketRequestWrapper) throws Exception;

    /**
     * 核心执行方法
     * @param requestWrapper
     * @throws Exception
     */
    void process(RequestWrapper requestWrapper) throws Exception;

    /**
     * 执行器启动方法
     */
    void start();

    /**
     * 执行器资源释放/关闭方法
     */
    void shutdown();
}
