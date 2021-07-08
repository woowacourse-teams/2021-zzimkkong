package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends RuntimeException {
    private final HttpStatus status;

    public AuthorizationException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
