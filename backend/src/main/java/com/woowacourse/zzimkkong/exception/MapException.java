package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class MapException extends RuntimeException {
    private final HttpStatus status;

    public MapException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
