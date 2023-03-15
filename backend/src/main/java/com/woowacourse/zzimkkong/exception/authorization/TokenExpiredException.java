package com.woowacourse.zzimkkong.exception.authorization;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends ZzimkkongException {
    private static final String MESSAGE = "로그인 인증 유효기간이 만료되었습니다. 다시 로그인 해주세요.";

    public TokenExpiredException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
