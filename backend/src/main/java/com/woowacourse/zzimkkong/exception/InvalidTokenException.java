package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AuthorizationException {
    private static final String MESSAGE = "유효한 토큰이 아닙니다.";

    public InvalidTokenException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
