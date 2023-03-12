package com.woowacourse.zzimkkong.exception.member;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class ConfirmationNewPasswordMismatchException extends ZzimkkongException {
    private static final String MESSAGE = "새 비밀번호가 확인란과 일치하지 않습니다.";

    public ConfirmationNewPasswordMismatchException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
