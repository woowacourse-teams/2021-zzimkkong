package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SpaceTest {
    @Test
    void update() {
        Member member = new Member(EMAIL, PASSWORD, ORGANIZATION);
        Map map = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, member);

        Setting setting = new Setting.Builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .build();
        Space space = new Space.Builder()
                .name("와우")
                .color("색깔입니다")
                .description("잠실짱")
                .area("area")
                .setting(setting)
                .map(map)
                .build();

        Setting updateSetting = new Setting.Builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationEnable(true)
                .build();
        Space updateSpace = new Space.Builder()
                .name("우와")
                .color("색깔")
                .description("루터짱")
                .area("area")
                .setting(updateSetting)
                .map(map)
                .build();

        space.update(updateSpace);
        assertThat(space).usingRecursiveComparison().isEqualTo(updateSpace);
    }

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

    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    @DisplayName("예약 시작 시간의 단위가 타당하면 false를 반환한다.")
    void isCorrectTimeUnit(int minute) {
        Setting availableTimeSetting = new Setting.Builder()
                .reservationTimeUnit(10)
                .build();
        Space availableTimeSpace = new Space.Builder().setting(availableTimeSetting).build();

        boolean actual = availableTimeSpace.isIncorrectTimeUnit(minute);

        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {9, 11})
    @DisplayName("예약 시작 시간의 단위가 타당하지 않다면 true를 반환한다.")
    void isCorrectTimeUnitFail(int minute) {
        Setting availableTimeSetting = new Setting.Builder()
                .reservationTimeUnit(10)
                .build();
        Space availableTimeSpace = new Space.Builder().setting(availableTimeSetting).build();

        boolean actual = availableTimeSpace.isIncorrectTimeUnit(minute);

        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 120})
    @DisplayName("예약 시간의 단위가 최소최대 예약시간단위 내에 있다면 false를 반환한다.")
    void isCorrectMinimumMaximumTimeUnit(int durationMinutes) {
        Setting availableTimeSetting = new Setting.Builder()
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(120)
                .build();
        Space availableTimeSpace = new Space.Builder().setting(availableTimeSetting).build();

        boolean actual = availableTimeSpace.isIncorrectMinimumMaximumTimeUnit(durationMinutes);

        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {9, 121})
    @DisplayName("예약 시간의 단위가 최소시간단위보다 작거나 최대시간단위보다 크다면 true를 반환한다.")
    void isCorrectMinimumMaximumTimeUnitFail(int durationMinutes) {
        Setting availableTimeSetting = new Setting.Builder()
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(120)
                .build();
        Space availableTimeSpace = new Space.Builder().setting(availableTimeSetting).build();

        boolean actual = availableTimeSpace.isIncorrectMinimumMaximumTimeUnit(durationMinutes);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("예약 시간의 단위가 공간의 timeUnit으로 나누어떨어지면 false를 반환한다.")
    void isNotDivideBy() {
        Setting availableTimeSetting = new Setting.Builder()
                .reservationTimeUnit(10)
                .build();
        Space availableTimeSpace = new Space.Builder().setting(availableTimeSetting).build();

        int minute = 100;
        boolean actual = availableTimeSpace.isNotDivideBy(minute);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("예약 시간의 단위가 공간의 timeUnit으로 나누어떨어지지 않으면 true를 반환한다.")
    void isNotDivideByFail() {
        Setting availableTimeSetting = new Setting.Builder()
                .reservationTimeUnit(10)
                .build();
        Space availableTimeSpace = new Space.Builder().setting(availableTimeSetting).build();

        int minute = 12;
        boolean actual = availableTimeSpace.isNotDivideBy(minute);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("예약이 가능한 공간이면 false를 반환한다")
    void isUnableToReserve() {
        Setting reservationEnableSetting = new Setting.Builder().reservationEnable(true).build();
        Space reservationEnableSpace = new Space.Builder().setting(reservationEnableSetting).build();

        assertThat(reservationEnableSpace.isUnableToReserve()).isFalse();
    }

    @Test
    @DisplayName("예약이 불가능한 공간이면 true를 반환한다")
    void isUnableToReserveFail() {
        Setting reservationUnableSetting = new Setting.Builder().reservationEnable(false).build();
        Space reservationUnableSpace = new Space.Builder().setting(reservationUnableSetting).build();

        assertThat(reservationUnableSpace.isUnableToReserve()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"MONDAY", "WEDNESDAY"})
    @DisplayName("해당 요일에 예약이 가능하면 false를 반환한다")
    void isClosedOn(DayOfWeek dayOfWeek) {
        Setting setting = new Setting.Builder().enabledDayOfWeek("monday, wednesday").build();
        Space space = new Space.Builder().setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"TUESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    @DisplayName("해당 요일에 예약이 불가능하면 true를 반환한다")
    void isClosedOnFail(DayOfWeek dayOfWeek) {
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
}
