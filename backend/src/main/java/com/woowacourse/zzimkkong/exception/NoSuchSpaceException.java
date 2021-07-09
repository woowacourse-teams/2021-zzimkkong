package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class NoSuchSpaceException extends SpaceException {
    private static final String MESSAGE = "해당 공간이 존재하지 않습니다.";

    public NoSuchSpaceException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}

