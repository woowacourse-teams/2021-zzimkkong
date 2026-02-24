package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InsufficientGroupException extends ZzimkkongException {
    private static final String MESSAGE = "해당 공간을 예약할 수 있는 권한이 없습니다.";

    public InsufficientGroupException() {
        super(MESSAGE, HttpStatus.FORBIDDEN);
    }
}
