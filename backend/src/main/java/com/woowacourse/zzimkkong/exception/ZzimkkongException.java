package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class ZzimkkongException extends RuntimeException {
    private final HttpStatus status;

    public ZzimkkongException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ZzimkkongException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
