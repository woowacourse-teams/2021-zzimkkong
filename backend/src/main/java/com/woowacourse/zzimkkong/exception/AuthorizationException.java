package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends RuntimeException {
    private final HttpStatus status;

    public AuthorizationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
