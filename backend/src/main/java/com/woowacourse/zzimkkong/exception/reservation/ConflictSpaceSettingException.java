package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class ConflictSpaceSettingException extends ZzimkkongException {
    private static final String MESSAGE = "공간의 예약조건을 확인해주세요.";

    public ConflictSpaceSettingException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
