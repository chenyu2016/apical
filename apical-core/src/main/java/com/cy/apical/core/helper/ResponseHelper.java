package com.cy.apical.core.helper;

import com.cy.apical.common.enums.ResponseCode;
import com.cy.apical.core.context.ApicalHttpResponse;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

/**
 * @Author ChenYu
 * @Date 2022/7/10 上午1:38
 * @Describe 响应的辅助类
 * @Version 1.0
 */
public class ResponseHelper {

    /**
     * 获取http响应对象
     * @param responseCode
     * @return
     */
    public static FullHttpResponse getHttpResponse(ResponseCode responseCode) {
        ApicalHttpResponse resRapidResponse = ApicalHttpResponse.buildResponse(responseCode);
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                Unpooled.wrappedBuffer(resRapidResponse.getContent().getBytes()));

        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        return httpResponse;
    }
}
