package com.woowacourse.zzimkkong.exception.setting;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidPriorityException extends ZzimkkongException {
    public InvalidPriorityException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
