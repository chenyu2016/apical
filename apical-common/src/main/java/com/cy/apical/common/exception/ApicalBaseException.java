package com.cy.apical.common.exception;

import com.cy.apical.common.enums.ResponseCode;

/**
 * @Author ChenYu
 * @Date 2022/3/6 下午6:30
 * @Describe 网关基础异常类
 * @Version 1.0
 */
public class ApicalBaseException extends RuntimeException{

    private static final long serialVersionUID = 9126367217681289415L;

    public ApicalBaseException() {
    }

    protected ResponseCode code;

    public ApicalBaseException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }

    public ApicalBaseException(String message, Throwable cause, ResponseCode code) {
        super(message, cause);
        this.code = code;
    }

    public ApicalBaseException(ResponseCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public ApicalBaseException(String message, Throwable cause,
                              boolean enableSuppression, boolean writableStackTrace, ResponseCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }
}
