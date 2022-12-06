package com.woowacourse.zzimkkong.exception.member;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class DuplicateUserNameException extends InputFieldException {
    private static final String MESSAGE = "이미 사용 중인 유저 이름입니다.";

    public DuplicateUserNameException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, USER_NAME);
    }
}
