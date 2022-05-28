package com.cy.apical.common.exception;

import com.cy.apical.common.enums.ResponseCode;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午6:42
 * @Describe 响应异常基础类
 * @Version 1.0
 */
public class ApicalResponseException extends ApicalBaseException{
    private static final long serialVersionUID = 5573540947515201183L;

    public ApicalResponseException() {
        this(ResponseCode.INTERNAL_ERROR);
    }

    public ApicalResponseException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public ApicalResponseException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.code = code;
    }
}
