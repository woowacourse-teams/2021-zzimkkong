package com.woowacourse.zzimkkong.exception.member;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class MemberException extends ZzimkkongException {
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    public MemberException(final String message, final HttpStatus status, final String field) {
        super(message, status, field);
    }
}
