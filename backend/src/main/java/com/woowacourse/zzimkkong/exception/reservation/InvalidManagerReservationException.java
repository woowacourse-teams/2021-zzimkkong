package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidManagerReservationException extends ZzimkkongException {
    private static final String MESSAGE = "예약자 이메일 혹은 이름 & 패스워드가 제대로 입력되었는지 확인해주세요";

    public InvalidManagerReservationException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
