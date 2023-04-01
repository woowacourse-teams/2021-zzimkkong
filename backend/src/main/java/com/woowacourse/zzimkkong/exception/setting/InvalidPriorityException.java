package com.woowacourse.zzimkkong.exception.setting;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.SETTING_NEGATIVE_PRIORITY_MESSAGE;

public class InvalidPriorityException extends ZzimkkongException {
    public InvalidPriorityException() {
        super(SETTING_NEGATIVE_PRIORITY_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
