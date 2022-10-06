package com.cy.apical.core.context;


/**
 * @Author ChenYu
 * @Date 2022/7/9 下午3:41
 * @Describe 可变的参数修改接口
 * @Version 1.0
 */
public interface ApicalRequestMutable {


    /**
     * 设置请求host
     * @param host
     */
    void setModifyHost(String host);
    String getModifyHost(String host);

    /**
     * 设置请求路径
     * @param path
     */
    void setModifyPath(String path);
    String getModifyPath();

    /**
     * 添加请求头信息
     * @param name
     * @param value
     */
    void addHeader(CharSequence name, String value);

    /**
     * 设置请求头信息
     * @param name
     * @param value
     */
    void setHeader(CharSequence name, String value);

    /**
     * 添加请求参数
     * @param name
     * @param value
     */
    void addQueryParam(String name, String value);


    /**
     * 设置超时时间
     * @param requestTimeout
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * 设置 cookie
     * @param cookie
     */
    void addOrReplaceCookie(org.asynchttpclient.cookie.Cookie cookie);

    /**
     * 构建转发请求的请求对象
     * @return
     */
    Object build();

    /**
     * 获得最终的路由路径
     * @return
     */
    String getFinalUrl();
}
