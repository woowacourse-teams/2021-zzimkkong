package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationHeaderUninvolvedException extends AuthorizationException {
    private static final String MESSAGE = "인증을 위한 헤더 값이 없습니다.";

    public AuthorizationHeaderUninvolvedException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
