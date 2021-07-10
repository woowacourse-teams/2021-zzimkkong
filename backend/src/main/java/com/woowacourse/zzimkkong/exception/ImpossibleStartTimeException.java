package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class ImpossibleStartTimeException extends ReservationException {
    private static final String MESSAGE = "시작 시간을 확인해주세요.";

    public ImpossibleStartTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
