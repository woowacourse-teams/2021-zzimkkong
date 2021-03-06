package com.woowacourse.zzimkkong.exception.member;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoSuchOAuthMemberException extends ZzimkkongException {
    private static final String MESSAGE = "소셜 로그인 회원이 아닙니다. 회원가입을 진행합니다.";

    private final String email;

    public NoSuchOAuthMemberException(String email) {
        super(MESSAGE, HttpStatus.NOT_FOUND);
        this.email = email;
    }
}
