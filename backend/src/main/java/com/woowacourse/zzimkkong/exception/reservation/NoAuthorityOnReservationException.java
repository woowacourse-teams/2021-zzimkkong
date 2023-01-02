package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoAuthorityOnReservationException extends ZzimkkongException {
    public static final String MESSAGE = "해당 예약에 대한 권한이 없습니다";

    public NoAuthorityOnReservationException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
