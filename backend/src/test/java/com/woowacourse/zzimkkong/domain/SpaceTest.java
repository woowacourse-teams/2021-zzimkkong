package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.woowacourse.zzimkkong.Constants.THE_DAY_AFTER_TOMORROW;
import static org.assertj.core.api.Assertions.assertThat;

public class SpaceTest {
    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 내에 있다면 false를 반환한다")
    void isNotBetweenAvailableTime() {
        Setting availableTimeSetting = new Setting.Builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .build();
        Space availableTimeSpace = new Space.Builder().setting(availableTimeSetting).build();

        LocalDateTime startDateTime = THE_DAY_AFTER_TOMORROW.atTime(10, 0);
        LocalDateTime endDateTime = THE_DAY_AFTER_TOMORROW.atTime(18, 0);
        boolean actual = availableTimeSpace.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 외에 있다면 true를 반환한다")
    void isNotBetweenAvailableTimeFail() {
        Setting availableTimeSetting = new Setting.Builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .build();
        Space availableTimeSpace = new Space.Builder().setting(availableTimeSetting).build();

        LocalDateTime startDateTime = THE_DAY_AFTER_TOMORROW.atTime(9, 59);
        LocalDateTime endDateTime = THE_DAY_AFTER_TOMORROW.atTime(18, 1);
        boolean actual = availableTimeSpace.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("예약이 가능한 공간이면 false를 반환한다")
    void isAbleToReserve() {
        Setting reservationEnableSetting = new Setting.Builder().reservationEnable(true).build();
        Space reservationEnableSpace = new Space.Builder().setting(reservationEnableSetting).build();

        assertThat(reservationEnableSpace.isUnableToReserve()).isFalse();
    }

    @Test
    @DisplayName("예약이 불가능한 공간이면 true를 반환한다")
    void isUnableToReserve() {
        Setting reservationUnableSetting = new Setting.Builder().reservationEnable(false).build();
        Space reservationUnableSpace = new Space.Builder().setting(reservationUnableSetting).build();

        assertThat(reservationUnableSpace.isUnableToReserve()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"TUESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    @DisplayName("해당 요일에 예약이 불가능하면 true를 반환한다")
    void isClosedOn_Disabled(DayOfWeek dayOfWeek) {
        Setting setting = new Setting.Builder().enabledDayOfWeek("monday, wednesday").build();
        Space space = new Space.Builder().setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class)
    @DisplayName("예약 가능한 요일이 null이면 모든 요일에 대해서 true를 반환한다")
    void isClosedOn_nullEnabledDayOfWeek(DayOfWeek dayOfWeek) {
        Setting setting = new Setting.Builder().enabledDayOfWeek(null).build();
        Space space = new Space.Builder().setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"MONDAY", "WEDNESDAY"})
    @DisplayName("해당 요일에 예약이 가능하면 false를 반환한다")
    void isClosedOn_Enable(DayOfWeek dayOfWeek) {
        Setting setting = new Setting.Builder().enabledDayOfWeek("monday, wednesday").build();
        Space space = new Space.Builder().setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isFalse();
    }

    //TODO 예약시간단위 도메인테스트 추가
}
