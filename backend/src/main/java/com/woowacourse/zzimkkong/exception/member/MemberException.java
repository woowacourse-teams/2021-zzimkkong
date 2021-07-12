package com.woowacourse.zzimkkong.exception.member;

import org.springframework.http.HttpStatus;

public class MemberException extends RuntimeException {
    private final HttpStatus status;

    public MemberException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
