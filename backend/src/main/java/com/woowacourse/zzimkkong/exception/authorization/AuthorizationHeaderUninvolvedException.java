package com.woowacourse.zzimkkong.exception.authorization;

import org.springframework.http.HttpStatus;

public class AuthorizationHeaderUninvolvedException extends AuthorizationException {
    private static final String MESSAGE = "인증되지 않은 사용자입니다.";

    public AuthorizationHeaderUninvolvedException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED, TOKEN);
    }
}
