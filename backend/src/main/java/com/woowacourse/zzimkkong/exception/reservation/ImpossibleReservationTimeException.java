package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class ImpossibleReservationTimeException extends InputFieldException {
    private static final String MESSAGE = "해당 시간에 이미 예약이 존재합니다.";

    public ImpossibleReservationTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, START_DATE_TIME);
    }
}
