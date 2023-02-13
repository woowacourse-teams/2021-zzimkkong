package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidNonLoginReservationException extends ZzimkkongException {
    private static final String MESSAGE = "예약자 이름, 패스워드가 제대로 입력되었는지 확인해주세요";

    public InvalidNonLoginReservationException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
