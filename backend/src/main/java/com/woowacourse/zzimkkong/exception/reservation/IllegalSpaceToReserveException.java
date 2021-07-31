package com.woowacourse.zzimkkong.exception.reservation;

import org.springframework.http.HttpStatus;

import static com.woowacourse.zzimkkong.exception.space.SpaceException.RESERVATION_ENABLE;

public class IllegalSpaceToReserveException extends ReservationException {
    private static final String MESSAGE = "현재 예역이 불가능한 공간입니다.";

    public IllegalSpaceToReserveException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, RESERVATION_ENABLE);
    }
}
