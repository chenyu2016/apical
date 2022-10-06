package com.cy.apical.core.context;

import com.cy.apical.common.enums.ResponseCode;
import com.cy.apical.common.util.JSONUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.codec.http.*;
import lombok.Data;
import org.asynchttpclient.Response;


/**
 * @Author ChenYu
 * @Date 2022/7/10 上午1:08
 * @Describe 网关http响应封装类
 * @Version 1.0
 */
@Data
public class ApicalHttpResponse {

    //	响应头
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();

    //	额外的响应结果
    private final HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();

    //	返回的响应内容
    private String content;

    //	返回响应状态码
    private HttpResponseStatus httpResponseStatus;

    //	响应对象
    private Response futureResponse;


    /**
     * 设置响应头信息
     * @param key
     * @param val
     */
    public void putHeader(CharSequence key, CharSequence val) {
        responseHeaders.add(key, val);
    }

    public static ApicalHttpResponse buildResponse(Response futureResponse) {
        ApicalHttpResponse apicalHttpResponse = new ApicalHttpResponse();
        apicalHttpResponse.setFutureResponse(futureResponse);
        apicalHttpResponse.setHttpResponseStatus(HttpResponseStatus.valueOf(futureResponse.getStatusCode()));
        return apicalHttpResponse;
    }

    public static ApicalHttpResponse buildResponse(ResponseCode code, Object... args) {
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, code.getStatus().code());
        objectNode.put(JSONUtil.CODE, code.getCode());
        objectNode.put(JSONUtil.MESSAGE, code.getMessage());
        ApicalHttpResponse rapidResponse = new ApicalHttpResponse();
        rapidResponse.setHttpResponseStatus(code.getStatus());
        rapidResponse.putHeader(HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        rapidResponse.setContent(JSONUtil.toJSONString(objectNode));
        return rapidResponse;
    }

    public static ApicalHttpResponse buildResponseObj(Object data) {
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, ResponseCode.SUCCESS.getStatus().code());
        objectNode.put(JSONUtil.CODE, ResponseCode.SUCCESS.getCode());
        objectNode.putPOJO(JSONUtil.DATA, data);
        ApicalHttpResponse rapidResponse = new ApicalHttpResponse();
        rapidResponse.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        rapidResponse.putHeader(HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        rapidResponse.setContent(JSONUtil.toJSONString(objectNode));
        return rapidResponse;
    }

}
