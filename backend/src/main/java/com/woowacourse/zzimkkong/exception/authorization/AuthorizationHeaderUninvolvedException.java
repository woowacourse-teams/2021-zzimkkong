package com.woowacourse.zzimkkong.exception.authorization;

import org.springframework.http.HttpStatus;

public class AuthorizationHeaderUninvolvedException extends AuthorizationException {
    private static final String MESSAGE = "로그인이 필요한 서비스입니다.";

    public AuthorizationHeaderUninvolvedException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED, TOKEN);
    }
}
