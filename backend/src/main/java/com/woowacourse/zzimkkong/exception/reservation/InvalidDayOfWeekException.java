package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidDayOfWeekException extends ZzimkkongException {
    private static final String MESSAGE = "해당 요일에 예약이 불가능한 공간입니다.";

    public InvalidDayOfWeekException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
