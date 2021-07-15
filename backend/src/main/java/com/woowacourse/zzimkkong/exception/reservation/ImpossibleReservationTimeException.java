package com.woowacourse.zzimkkong.exception.reservation;

import org.springframework.http.HttpStatus;

public class ImpossibleReservationTimeException extends ReservationException {
    private static final String MESSAGE = "예약할 수 없는 시간입니다.";

    public ImpossibleReservationTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
