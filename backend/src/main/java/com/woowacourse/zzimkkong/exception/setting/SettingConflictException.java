package com.woowacourse.zzimkkong.exception.setting;

import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class SettingConflictException extends ZzimkkongException {
    private static final String MESSAGE_FORMAT = "공간 예약 조건들의 시간대가 겹칩니다. \n겹치는 시간대 정보: %s VS %s";

    public SettingConflictException(final Setting currentSetting, final Setting nextSetting) {
        super(String.format(
                MESSAGE_FORMAT,
                        currentSetting.getSettingTimeSlot(),
                        nextSetting.getSettingTimeSlot()),
                HttpStatus.NOT_FOUND);
    }
}
