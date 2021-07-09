package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class NoSuchEmailException extends MemberException {
    private static final String MESSAGE = "이메일 혹은 비밀번호를 확인해주세요.";

    public NoSuchEmailException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
