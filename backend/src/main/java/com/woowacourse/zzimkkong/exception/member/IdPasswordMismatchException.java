package com.woowacourse.zzimkkong.exception.member;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class IdPasswordMismatchException extends InputFieldException {
    private static final String MESSAGE = "이메일 혹은 비밀번호를 확인해주세요.";

    public IdPasswordMismatchException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, PASSWORD);
    }
}
