package com.woowacourse.zzimkkong.exception.reservation;

import org.springframework.http.HttpStatus;

public class NoSuchReservationException extends ReservationException {
    private static final String MESSAGE = "존재하지 않는 예약입니다.";

    public NoSuchReservationException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, RESERVATION_ID);
    }
}
