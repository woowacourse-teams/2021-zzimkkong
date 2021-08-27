package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class ReservationExistOnSpaceException extends ZzimkkongException {
    private static final String MESSAGE = "예약이 존재하는 공간은 삭제할 수 없습니다.";

    public ReservationExistOnSpaceException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
