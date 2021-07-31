package com.woowacourse.zzimkkong.exception.space;

import org.springframework.http.HttpStatus;

public class NoSuchDayOfWeekException extends SpaceException {
    private static final String MESSAGE = "존재하지 않는 요일입니다.";

    public NoSuchDayOfWeekException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, DAY_OF_WEEK);
    }
}
