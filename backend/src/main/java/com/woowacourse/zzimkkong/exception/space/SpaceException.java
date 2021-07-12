package com.woowacourse.zzimkkong.exception.space;

import org.springframework.http.HttpStatus;

public class SpaceException extends RuntimeException {
    private final HttpStatus status;

    public SpaceException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
