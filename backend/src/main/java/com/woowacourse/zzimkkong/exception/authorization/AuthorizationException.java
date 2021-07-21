package com.woowacourse.zzimkkong.exception.authorization;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class AuthorizationException extends ZzimkkongException {
    public static final String TOKEN = "accessToken";
    public static final String AUTHORITY_ON_MAP = "authorityOnMap";

    public AuthorizationException(final String message, final HttpStatus status, final String field) {
        super(message, status, field);
    }
}
