package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.CommonFixture.BE;
import static com.woowacourse.zzimkkong.CommonFixture.THE_DAY_AFTER_TOMORROW;
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
    @EnumSource(value = DayOfWeek.class, names = {"TUESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    void isClosedOn_Disabled(DayOfWeek dayOfWeek) {
        Setting setting = settingBuilder.enabledDayOfWeek("monday, wednesday").build();
        Space space = spaceBuilder.setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isTrue();
    }

    @DisplayName("예약 가능한 요일이 null이면 모든 요일에 대해서 true를 반환한다")
    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class)
    void isClosedOn_nullEnabledDayOfWeek(DayOfWeek dayOfWeek) {
        Setting setting = settingBuilder.enabledDayOfWeek(null).build();
        Space space = spaceBuilder.setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isTrue();
    }

    @DisplayName("해당 요일에 예약이 가능하면 false를 반환한다")
    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"MONDAY", "WEDNESDAY"})
    void isClosedOn_Enable(DayOfWeek dayOfWeek) {
        Setting setting = settingBuilder.enabledDayOfWeek("monday, wednesday").build();
        Space space = spaceBuilder.setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isFalse();
    }

    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 내에 있다면 false를 반환한다")
    void isNotBetweenAvailableTime() {
        LocalDateTime startDateTime = THE_DAY_AFTER_TOMORROW.atTime(10,0);
        LocalDateTime endDateTime = THE_DAY_AFTER_TOMORROW.atTime(18,0);
        boolean actual = BE.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 외에 있다면 true를 반환한다")
    void isNotBetweenAvailableTimeFail() {
        LocalDateTime startDateTime = THE_DAY_AFTER_TOMORROW.atTime(9,59);
        LocalDateTime endDateTime = THE_DAY_AFTER_TOMORROW.atTime(18,1);
        boolean actual = BE.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isTrue();
    }
}
