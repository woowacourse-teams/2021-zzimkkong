package com.woowacourse.zzimkkong.exception.authorization;

import org.springframework.http.HttpStatus;

public class NoAuthorityOnMapException extends AuthorizationException {
    private static final String MESSAGE = "해당 맵에 대한 권한이 없습니다.";

    public NoAuthorityOnMapException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED, AUTHORITY_ON_MAP);
    }
}
