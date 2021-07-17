package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ExceptionField;
import org.springframework.http.HttpStatus;

public class NonMatchingStartAndEndDateException extends ReservationException {
    private static final String MESSAGE = "시작 날짜와 종료 날짜는 같아야 합니다.";

    public NonMatchingStartAndEndDateException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, ExceptionField.START_DATETIME.fieldName());
    }
}
