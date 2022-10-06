package com.cy.apical.core.context;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author ChenYu
 * @Date 2022/3/19 下午10:59
 * @Describe socket 请求包装类
 * @Version 1.0
 */
public class SocketRequestWrapper extends RequestWrapper {

    private Object msg;

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    @Override
    public void errorCallBackWrite(Object o) {

    }
}
