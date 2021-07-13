package com.woowacourse.zzimkkong.exception.authorization;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AuthorizationException {
    private static final String MESSAGE = "다시 로그인해주세요.";

    public TokenExpiredException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
