package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoAuthorityOnMapException extends ZzimkkongException {
    private static final String MESSAGE = "해당 맵에 대한 권한이 없습니다.";

    public NoAuthorityOnMapException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
