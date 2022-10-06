package com.cy.apical.core.context;

import com.cy.apical.common.constants.BasicConst;
import com.cy.apical.common.util.TimeUtil;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.RequestBuilder;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @Author ChenYu
 * @Date 2022/7/9 下午3:53
 * @Describe
 * @Version 1.0
 */
public class ApicalHttpRequest implements ApicalRequestMutable {

    /**
     * 请求头中 必须要有的属性uniqueId 标示服务的唯一性 serviceId:version 灰度 红蓝
     */
    @Getter
    private final String uniqueId;

    /**
     * 进入网关的开始时间
     * 统计耗时
     */
    @Getter
    private final long beginTime;

    @Getter
    private final Charset charset;

    /**
     * 客户端ip 流控 黑白名单
     */
    @Getter
    private final String clientIp;

    /**
     * 请求地址 ip:port
     */
    @Getter
    private final String host;

    /**
     * 请求路径
     */
    @Getter
    private final String path;

    /**
     * 请求全路径
     */
    @Getter
    private final String uri;

    /**
     * 对于 http 请求的 请求类型
     * get post put del 。。。
     */
    @Getter
    private final HttpMethod method;

    /**
     * 请求格式
     */
    @Getter
    private final String contentType;

    /**
     * 对于http 请求头信息
     */
    @Getter
    private final HttpHeaders httpHeaders;

    /**
     * 参数解析器
     */
    @Getter
    private final QueryStringDecoder queryStringDecoder;

    /**
     *	FullHttpRequest
     */
    @Getter
    private final FullHttpRequest fullHttpRequest;

    /**
     * 	请求体
     */
    private String body;

    /**
     * 	请求对象里面的cookie：
     */
    private Map<String, Cookie> cookieMap;

    /**
     * 	请求的时候定义的post参数集合
     */
    private Map<String, List<String>> postParameters;


    /**
     * 	可修改的scheme
     */
    private String modifyScheme;

    /**
     * 	可修改的host
     */
    private String modifyHost;

    /**
     * 	可修改的path
     */
    private String modifyPath;

    /**
     * 	构建下游请求时的Http请构建器
     */
    private final RequestBuilder requestBuilder;

    public ApicalHttpRequest(String uniqueId, String clientIp, String host,
                             String uri, HttpMethod method, String contentType, HttpHeaders httpHeaders,
                             Charset charset, FullHttpRequest fullHttpRequest) {
        this.uniqueId = uniqueId;
        this.beginTime = TimeUtil.currentTimeMillis();
        this.clientIp = clientIp;
        this.host = host;
        this.uri = uri;
        this.method = method;
        this.contentType = contentType;
        this.httpHeaders = httpHeaders;
        this.charset = charset;
        this.queryStringDecoder = new QueryStringDecoder(uri, charset);
        this.path = this.queryStringDecoder.path();
        this.fullHttpRequest = fullHttpRequest;

        this.modifyHost = host;
        this.modifyPath = path;
        this.modifyScheme = BasicConst.HTTP_PREFIX_SEPARATOR;
        this.requestBuilder = new RequestBuilder();
        this.requestBuilder.setMethod(getMethod().name());
        this.requestBuilder.setHeaders(getHttpHeaders());
        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());
        ByteBuf contentBuffer = fullHttpRequest.content();
        if(Objects.nonNull(contentBuffer)) {
            this.requestBuilder.setBody(contentBuffer.nioBuffer());
        }
    }

    @Override
    public void setModifyHost(String host) {
        this.modifyHost = host;
    }

    @Override
    public String getModifyHost(String host) {
        return modifyHost;
    }

    @Override
    public void setModifyPath(String path) {
        this.modifyPath = path;
    }

    @Override
    public String getModifyPath() {
        return modifyPath;
    }


    @Override
    public void addHeader(CharSequence name, String value) {
        requestBuilder.addHeader(name,value);
    }

    @Override
    public void setHeader(CharSequence name, String value) {
        requestBuilder.setHeader(name, value);
    }

    @Override
    public void addQueryParam(String name, String value) {
        requestBuilder.addQueryParam(name, value);
    }

    @Override
    public void setRequestTimeout(int requestTimeout) {
        requestBuilder.setRequestTimeout(requestTimeout);
    }

    @Override
    public void addOrReplaceCookie(org.asynchttpclient.cookie.Cookie cookie) {
        requestBuilder.addOrReplaceCookie(cookie);
    }

    @Override
    public Object build() {
        return requestBuilder.build();
    }

    @Override
    public String getFinalUrl() {
        return modifyScheme + modifyHost + modifyPath;
    }


    /**
     * 获取body
     * @return
     */
    public String getBody() {
        if(StringUtils.isEmpty(body)) {
            body = fullHttpRequest.content().toString(charset);
        }
        return body;
    }

    public Cookie getCookie(String name) {
        if(cookieMap == null) {
            cookieMap = new HashMap<>();
            String cookieStr = getHttpHeaders().get(HttpHeaderNames.COOKIE);
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            for(Cookie cookie : cookies) {
                cookieMap.put(name, cookie);
            }
        }
        return cookieMap.get(name);
    }

    /**
     * get 请求参数
     * @param name
     * @return
     */
    public List<String> getQueryParametersMultiple(String name){
        return queryStringDecoder.parameters().get(name);
    }

    /**
     * post 请求参数
     * @param name
     * @return
     */
    public List<String> getPostParametersMultiple(String name){
        String body = getBody();
        if(isFormPost()) {
            if(postParameters == null) {
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body, false);
                postParameters = paramDecoder.parameters();
            }

            if(postParameters == null || postParameters.isEmpty()) {
                return null;
            } else {
                return postParameters.get(name);
            }

        } else if (isJsonPost()) {
            try {
                return Lists.newArrayList(JsonPath.read(body, name).toString());
            } catch (Exception e) {
                e.printStackTrace();
                //	解析失败
            }
        }
        return null;
    }

    public boolean isFormPost() {
        return HttpMethod.POST.equals(method) &&
                (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()) ||
                        contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()));
    }

    public boolean isJsonPost() {
        return HttpMethod.POST.equals(method) &&
                contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString());
    }
}
