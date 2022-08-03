package com.woowacourse.zzimkkong.exception.setting;

import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoSettingAvailableException extends ZzimkkongException {
    private static final String MESSAGE_FORMAT = "예약이 불가능한 시간대 혹은 요일입니다" +
            System.getProperty("line.separator") +
            System.getProperty("line.separator") +
            "공간 [%s]의 예약 조건: " +
            System.getProperty("line.separator") +
            "%s";

    public NoSettingAvailableException(final Space space) {
        super(String.format(MESSAGE_FORMAT, space.getName(), space.getSpaceSettings()),
                HttpStatus.BAD_REQUEST);
    }
}
