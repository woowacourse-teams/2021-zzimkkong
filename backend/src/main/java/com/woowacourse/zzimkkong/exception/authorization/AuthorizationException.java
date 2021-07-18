package com.woowacourse.zzimkkong.exception.authorization;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class AuthorizationException extends ZzimkkongException {
    private static final String TOKEN = "accessToken";

    public AuthorizationException(final String message, final HttpStatus httpStatus) {
        super(message, httpStatus, TOKEN);
    }
}
