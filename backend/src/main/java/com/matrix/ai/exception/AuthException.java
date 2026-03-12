package com.matrix.ai.exception;

/**
 * 认证异常类
 */
public class AuthException extends RuntimeException {

    private final Integer code;

    public AuthException(String message) {
        super(message);
        this.code = 500;
    }

    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
