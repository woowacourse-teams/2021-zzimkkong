package com.woowacourse.zzimkkong.exception.reservation;

import org.springframework.http.HttpStatus;

public class ImpossibleEndTimeException extends ReservationException {
    private static final String MESSAGE = "종료 시간을 확인해주세요.";

    public ImpossibleEndTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, "endDateTime");
    }
}
