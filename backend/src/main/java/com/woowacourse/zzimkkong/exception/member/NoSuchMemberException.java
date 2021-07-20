package com.woowacourse.zzimkkong.exception.member;

import org.springframework.http.HttpStatus;

public class NoSuchMemberException extends MemberException {
    private static final String MESSAGE = "존재하지 않는 회원입니다.";

    public NoSuchMemberException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, EMAIL);
    }
}
