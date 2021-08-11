package com.woowacourse.zzimkkong.exception.authorization;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends ZzimkkongException {
    private static final String MESSAGE = "로그인이 필요한 서비스입니다.";

    public TokenExpiredException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
