package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class ReservationException extends RuntimeException {
    private final HttpStatus status;

    public ReservationException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

