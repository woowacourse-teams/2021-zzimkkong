package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.DayOfWeek;

import static org.assertj.core.api.Assertions.assertThat;

public class SpaceTest {
    private final Setting.Builder settingBuilder = new Setting.Builder();
    private final Space.Builder spaceBuilder = new Space.Builder();

    @DisplayName("예약이 불가능한 공간이면 true, 가능하면 false")
    @Test
    void isUnableToReserve() {
        Setting reservationEnableSetting = settingBuilder.reservationEnable(true).build();
        Setting reservationUnableSetting = settingBuilder.reservationEnable(false).build();

        Space reservationEnableSpace = spaceBuilder.setting(reservationEnableSetting).build();
        Space reservationUnableSpace = spaceBuilder.setting(reservationUnableSetting).build();

        assertThat(reservationEnableSpace.isUnableToReserve()).isFalse();
        assertThat(reservationUnableSpace.isUnableToReserve()).isTrue();
    }

    @DisplayName("해당 요일에 예약이 불가능하면 true를 반환한다")
    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"MONDAY", "WEDNESDAY"})
    void isClosedOn_disable(DayOfWeek dayOfWeek) {
        Setting setting = settingBuilder.disabledWeekdays("monday, wednesday").build();
        Space space = spaceBuilder.setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isTrue();
    }

    @DisplayName("해당 요일에 예약이 가능하면 false를 반환한다")
    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"TUESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    void isClosedOn_able(DayOfWeek dayOfWeek) {
        Setting setting = settingBuilder.disabledWeekdays("monday, wednesday").build();
        Space space = spaceBuilder.setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isFalse();
    }
}
