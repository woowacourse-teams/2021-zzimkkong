package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ExceptionField;
import org.springframework.http.HttpStatus;

public class NoSuchReservationException extends ReservationException {
    private static final String MESSAGE = "해당 예약이 존재하지 않습니다.";

    public NoSuchReservationException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, ExceptionField.RESERVATION_ID.fieldName());
    }
}
