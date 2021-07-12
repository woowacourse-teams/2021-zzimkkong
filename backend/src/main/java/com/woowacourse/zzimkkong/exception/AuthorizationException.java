package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends RuntimeException {
    private final HttpStatus httpStatus;

    public AuthorizationException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
