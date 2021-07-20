package com.woowacourse.zzimkkong.exception.map;

import org.springframework.http.HttpStatus;

public class NoSuchMapException extends MapException {
    private static final String MESSAGE = "존재하지 않는 맵입니다.";

    public NoSuchMapException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, MAP_ID);
    }
}
