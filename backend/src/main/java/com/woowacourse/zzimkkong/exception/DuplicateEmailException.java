package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends MemberException {

    private static final String MESSAGE = "이미 사용 중인 이메일입니다";

    public DuplicateEmailException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
