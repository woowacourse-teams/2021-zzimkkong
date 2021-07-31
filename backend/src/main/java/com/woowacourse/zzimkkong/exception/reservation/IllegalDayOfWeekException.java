package com.woowacourse.zzimkkong.exception.reservation;

import org.springframework.http.HttpStatus;

import static com.woowacourse.zzimkkong.exception.space.SpaceException.DAY_OF_WEEK;

public class IllegalDayOfWeekException extends ReservationException {
    private static final String MESSAGE = "해당 요일에는 예약이 불가능 합니다.";

    public IllegalDayOfWeekException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, DAY_OF_WEEK);
    }
}
