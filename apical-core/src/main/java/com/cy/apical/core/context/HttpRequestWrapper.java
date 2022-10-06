package com.cy.apical.core.context;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @Author ChenYu
 * @Date 2022/3/19 下午10:52
 * @Describe http请求包装类
 * @Version 1.0
 */
public class HttpRequestWrapper extends RequestWrapper {

    private FullHttpRequest fullHttpRequest;

    public FullHttpRequest getFullHttpRequest() {
        return fullHttpRequest;
    }

    @Override
    public void errorCallBackWrite(Object o) {

    }

    public void setFullHttpRequest(FullHttpRequest fullHttpRequest) {
        this.fullHttpRequest = fullHttpRequest;
    }
}
