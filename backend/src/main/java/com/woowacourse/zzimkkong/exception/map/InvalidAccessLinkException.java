package com.woowacourse.zzimkkong.exception.map;

import org.springframework.http.HttpStatus;

public class InvalidAccessLinkException extends MapException {
    private static final String MESSAGE = "링크가 유효하지 않습니다. 맵 관리자에게 링크를 다시 요청하세요.";
    private static final String FIELD = "sharingMapId";

    public InvalidAccessLinkException() {
        super(MESSAGE, HttpStatus.NOT_FOUND, FIELD);
    }
}
