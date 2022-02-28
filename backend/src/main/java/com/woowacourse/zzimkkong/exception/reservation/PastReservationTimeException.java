package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class PastReservationTimeException extends InputFieldException {
    private static final String MESSAGE = "예약 시작 시간은 현재 시간보다 이후여야 합니다.";

    public PastReservationTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, START_DATE_TIME);
    }
}
