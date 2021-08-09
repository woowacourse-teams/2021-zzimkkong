package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {
    @Test
    @DisplayName("비밀번호가 일치하면 true를 반환한다.")
    void checkPassword() {
        //given
        Member member = new Member(EMAIL, PASSWORD, ORGANIZATION);

        //when, then
        assertThat(member.checkPassword(PASSWORD)).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 false를 반환한다.")
    void checkWrongPassword() {
        //given
        Member member = new Member(EMAIL, PASSWORD, ORGANIZATION);

        //when, then
        assertThat(member.checkPassword(PASSWORD + "%")).isFalse();
    }

    @Test
    @DisplayName("자신의 preset 중에 id가 같은 것을 반환한다.")
    void findPresetById() {
        //given, when
        Member member = new Member(1L, EMAIL, PASSWORD, ORGANIZATION);
        Setting setting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();
        Preset preset = new Preset(1L, PRESET_NAME1, setting, member);

        //then
        assertThat(member.findPresetById(1L))
                .isEqualTo(Optional.of(preset));
    }
}
