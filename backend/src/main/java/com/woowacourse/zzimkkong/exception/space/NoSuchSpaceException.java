package com.woowacourse.zzimkkong.exception.space;

import org.springframework.http.HttpStatus;

public class NoSuchSpaceException extends SpaceException {
    private static final String MESSAGE = "존재하지 않는 공간입니다.";

    public NoSuchSpaceException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, SPACE_ID);
    }
}
