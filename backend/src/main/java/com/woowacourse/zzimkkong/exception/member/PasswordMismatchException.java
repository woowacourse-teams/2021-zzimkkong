package com.woowacourse.zzimkkong.exception.member;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends MemberException {
    private static final String MESSAGE = "이메일 혹은 비밀번호를 확인해주세요.";

    public PasswordMismatchException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, PASSWORD);
    }
}
