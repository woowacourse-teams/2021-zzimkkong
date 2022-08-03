package com.woowacourse.zzimkkong.exception.setting;

import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

import static com.woowacourse.zzimkkong.infrastructure.message.MessageUtils.LINE_SEPARATOR;

public class NoSettingAvailableException extends ZzimkkongException {
    private static final String MESSAGE_FORMAT = "예약이 불가능한 시간대 혹은 요일입니다" +
            LINE_SEPARATOR +
            LINE_SEPARATOR +
            "공간 [%s]의 예약 조건: " +
            LINE_SEPARATOR +
            "%s";

    public NoSettingAvailableException(final Space space) {
        super(String.format(MESSAGE_FORMAT, space.getName(), space.getSpaceSettings()),
                HttpStatus.BAD_REQUEST);
    }
}
