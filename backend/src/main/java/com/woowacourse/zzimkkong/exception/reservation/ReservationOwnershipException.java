package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class ReservationOwnershipException extends ZzimkkongException {
    public static final String MESSAGE = "해당 예약에 대한 권한이 없습니다";

    public ReservationOwnershipException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
