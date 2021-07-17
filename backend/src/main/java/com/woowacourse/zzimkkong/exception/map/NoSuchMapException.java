package com.woowacourse.zzimkkong.exception.map;

import com.woowacourse.zzimkkong.exception.ExceptionField;
import org.springframework.http.HttpStatus;

public class NoSuchMapException extends MapException {
    private static final String MESSAGE = "해당 맵이 존재하지 않습니다.";

    public NoSuchMapException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, ExceptionField.MAP_ID.fieldName());
    }
}
