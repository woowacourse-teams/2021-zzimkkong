package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class ImpossibleStartEndTimeException extends InputFieldException {
    private static final String MESSAGE = "예약 종료 시간은 예약 시작 시간보다 이후여야 합니다.";

    public ImpossibleStartEndTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, END_DATE_TIME);
    }
}
