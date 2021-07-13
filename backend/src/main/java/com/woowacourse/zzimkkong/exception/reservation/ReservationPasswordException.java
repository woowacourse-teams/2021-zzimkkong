package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.reservation.ReservationException;
import org.springframework.http.HttpStatus;

public class ReservationPasswordException extends ReservationException {
    private static final String MESSAGE = "비밀번호를 확인해주세요.";

    public ReservationPasswordException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
