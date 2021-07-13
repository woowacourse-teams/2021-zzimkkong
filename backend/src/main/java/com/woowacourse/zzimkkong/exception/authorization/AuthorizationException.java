package com.woowacourse.zzimkkong.exception.authorization;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class AuthorizationException extends ZzimkkongException {
    public AuthorizationException(final String message, final HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
