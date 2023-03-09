package com.woowacourse.zzimkkong.exception.member;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends InputFieldException {
    private static final String MESSAGE = "비밀번호가 일치하지 않습니다.";

    public PasswordMismatchException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, PASSWORD);
    }
}
