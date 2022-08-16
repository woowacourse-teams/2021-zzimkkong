package com.woowacourse.zzimkkong.exception.setting;

import com.woowacourse.zzimkkong.domain.Settings;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

import static com.woowacourse.zzimkkong.infrastructure.message.MessageUtils.LINE_SEPARATOR;

public class MultipleSettingsException extends ZzimkkongException {
    private static final String MESSAGE_FORMAT = "예약이 다수의 조건들에 걸칩니다. 하나의 조건에만 해당되도록 예약 해주세요!" +
            LINE_SEPARATOR +
            LINE_SEPARATOR +
            "걸치는 예약 조건 목록: " + LINE_SEPARATOR +
            "%s ";

    public MultipleSettingsException(final Settings relevantSettings) {
        super(String.format(MESSAGE_FORMAT, relevantSettings.toString()), HttpStatus.BAD_REQUEST);
    }
}
