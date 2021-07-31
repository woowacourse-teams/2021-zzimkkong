package com.woowacourse.zzimkkong.exception.reservation;

import org.springframework.http.HttpStatus;

public class ConflictSpaceSettingException extends ReservationException {
    private static final String MESSAGE = "공간의 예약조건을 확인해주세요.";

    public ConflictSpaceSettingException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, SETTING);
    }
}
