package com.woowacourse.zzimkkong.exception.space;

import org.springframework.http.HttpStatus;

public class NoSuchWeekdayException extends SpaceException {
    private static final String MESSAGE = "존재하지 않는 요일입니다.";

    public NoSuchWeekdayException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, WEEKDAY);
    }
}
