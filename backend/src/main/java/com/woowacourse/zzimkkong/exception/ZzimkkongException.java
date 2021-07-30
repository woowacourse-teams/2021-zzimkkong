package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class ZzimkkongException extends RuntimeException {
    private final HttpStatus status;
    private final String field;

    public ZzimkkongException(final String message, final HttpStatus status, final String field) {
        super(message);
        this.status = status;
        this.field = field;
    }

    public ZzimkkongException(String message, Throwable cause, HttpStatus status, String field) {
        super(message, cause);
        this.status = status;
        this.field = field;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getField() {
        return field;
    }
}
