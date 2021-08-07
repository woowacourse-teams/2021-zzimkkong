package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class PresetTest {
    @Test
    @DisplayName("프리셋의 member가 동일한지 확인한다.")
    void hasSameMember() {
        //given
        Member member = new Member(EMAIL, PASSWORD, ORGANIZATION);
        Setting setting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Preset preset = new Preset(PRESET_NAME1, setting, member);

        //when, then
        assertThat(preset.isNotOwnedBy(member)).isTrue();
    }
}
