package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class ReservationPasswordException extends InputFieldException {
    private static final String MESSAGE = "비밀번호를 확인해주세요.";

    public ReservationPasswordException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, RESERVATION_PASSWORD);
    }
}
