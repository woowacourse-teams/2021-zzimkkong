package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AuthorizationException {
    private static final String MESSAGE = "토큰의 유효기간이 만료되었습니다.";

    public TokenExpiredException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
