package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class DeleteReservationInUseException extends ZzimkkongException {
    private static final String MESSAGE = "사용중인 예약은 삭제할 수 없습니다.";

    public DeleteReservationInUseException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
