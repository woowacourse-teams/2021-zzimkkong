package com.woowacourse.zzimkkong.exception.map;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidAccessLinkException extends ZzimkkongException {
    private static final String MESSAGE = "링크가 유효하지 않습니다. 맵 관리자에게 링크를 다시 요청하세요.";

    public InvalidAccessLinkException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
