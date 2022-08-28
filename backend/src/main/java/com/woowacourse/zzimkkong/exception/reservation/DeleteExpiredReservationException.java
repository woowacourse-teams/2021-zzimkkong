package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class DeleteExpiredReservationException extends ZzimkkongException {
    private static final String MESSAGE = "과거의 예약은 삭제할 수 없습니다.";

    public DeleteExpiredReservationException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
