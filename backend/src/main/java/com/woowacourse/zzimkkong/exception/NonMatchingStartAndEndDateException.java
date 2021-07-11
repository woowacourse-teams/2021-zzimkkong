package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class NonMatchingStartAndEndDateException extends ReservationException {
    private static final String MESSAGE = "시작 일시와 종료 일시는 같아야 합니다.";

    public NonMatchingStartAndEndDateException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
