package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class SpaceTest {
    @Test
    void update() {
        Member member = new Member(EMAIL, PW, ORGANIZATION);
        Map map = new Map(LUTHER_NAME, MAP_DRAWING_DATA, Constants.MAP_SVG, member);

        Setting setting = Setting.builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space space = Space.builder()
                .name("와우")
                .color("색깔입니다")
                .description("잠실짱")
                .area("area")
                .setting(setting)
                .map(map)
                .build();

        Setting updateSetting = Setting.builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(false)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space updateSpace = Space.builder()
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
    @DisplayName("공간의 Id가 동일한 Id면 true, 아니면 false를 반환한다")
    void hasSameId() {
        Space space = Space.builder()
                .id(1L)
                .build();

        assertThat(space.hasSameId(space.getId())).isTrue();
        assertThat(space.hasSameId(space.getId() + 1)).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {"10,12", "17,18"})
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 내에 있다면 false를 반환한다")
    void isNotBetweenAvailableTime(int startHour, int endHour) {
        Setting availableTimeSetting = Setting.builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space availableTimeSpace = Space.builder().setting(availableTimeSetting).build();

        LocalDateTime startDateTime = THE_DAY_AFTER_TOMORROW.atTime(startHour, 0);
        LocalDateTime endDateTime = THE_DAY_AFTER_TOMORROW.atTime(endHour, 0);
        boolean actual = availableTimeSpace.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 외에 있다면 true를 반환한다")
    void isNotBetweenAvailableTimeFail() {
        Setting availableTimeSetting = Setting.builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(18, 0))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space availableTimeSpace = Space.builder().setting(availableTimeSetting).build();

        LocalDateTime startDateTime = THE_DAY_AFTER_TOMORROW.atTime(9, 59);
        LocalDateTime endDateTime = THE_DAY_AFTER_TOMORROW.atTime(18, 1);
        boolean actual = availableTimeSpace.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    @DisplayName("예약 시작 시간의 단위가 타당하면 false를 반환한다.")
    void isCorrectTimeUnit(int minute) {
        Setting availableTimeSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space availableTimeSpace = Space.builder().setting(availableTimeSetting).build();

        boolean actual = availableTimeSpace.isNotDivisibleByTimeUnit(minute);

        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {9, 11})
    @DisplayName("예약 시작 시간의 단위가 타당하지 않다면 true를 반환한다.")
    void isCorrectTimeUnitFail(int minute) {
        Setting availableTimeSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space availableTimeSpace = Space.builder().setting(availableTimeSetting).build();

        boolean actual = availableTimeSpace.isNotDivisibleByTimeUnit(minute);

        assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 120})
    @DisplayName("예약 시간의 단위가 최소최대 예약시간단위 내에 있다면 false를 반환한다.")
    void isCorrectMinimumMaximumTimeUnit(int durationMinutes) {
        Setting availableTimeSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(120)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space availableTimeSpace = Space.builder().setting(availableTimeSetting).build();

        boolean actual = availableTimeSpace.isIncorrectMinimumMaximumTimeUnit(durationMinutes);

        assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {9, 121})
    @DisplayName("예약 시간의 단위가 최소시간단위보다 작거나 최대시간단위보다 크다면 true를 반환한다.")
    void isCorrectMinimumMaximumTimeUnitFail(int durationMinutes) {
        Setting availableTimeSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(120)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space availableTimeSpace = Space.builder().setting(availableTimeSetting).build();

        boolean actual = availableTimeSpace.isIncorrectMinimumMaximumTimeUnit(durationMinutes);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("예약 시간의 단위가 공간의 timeUnit으로 나누어떨어지면 false를 반환한다.")
    void isNotDivideBy() {
        Setting availableTimeSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space availableTimeSpace = Space.builder().setting(availableTimeSetting).build();

        int minute = 100;
        boolean actual = availableTimeSpace.isNotDivisibleByTimeUnit(minute);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("예약 시간의 단위가 공간의 timeUnit으로 나누어떨어지지 않으면 true를 반환한다.")
    void isNotDivideByFail() {
        Setting availableTimeSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space availableTimeSpace = Space.builder().setting(availableTimeSetting).build();

        int minute = 12;
        boolean actual = availableTimeSpace.isNotDivisibleByTimeUnit(minute);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("예약이 가능한 공간이면 false를 반환한다")
    void isUnableToReserve() {
        Setting reservationEnableSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(true)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space reservationEnableSpace = Space.builder().setting(reservationEnableSetting).build();

        assertThat(reservationEnableSpace.isUnableToReserve()).isFalse();
    }

    @Test
    @DisplayName("예약이 불가능한 공간이면 true를 반환한다")
    void isUnableToReserveFail() {
        Setting reservationUnableSetting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(false)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        Space reservationUnableSpace = Space.builder().setting(reservationUnableSetting).build();

        assertThat(reservationUnableSpace.isUnableToReserve()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"MONDAY", "WEDNESDAY"})
    @DisplayName("해당 요일에 예약이 가능하면 false를 반환한다")
    void isClosedOn(DayOfWeek dayOfWeek) {
        Setting setting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek("monday, wednesday")
                .build();
        Space space = Space.builder().setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"TUESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    @DisplayName("해당 요일에 예약이 불가능하면 true를 반환한다")
    void isClosedOnFail(DayOfWeek dayOfWeek) {
        Setting setting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek("monday, wednesday")
                .build();
        Space space = Space.builder().setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class)
    @DisplayName("예약 가능한 요일이 null이면 모든 요일에 대해서 true를 반환한다")
    void isClosedOn_nullEnabledDayOfWeek(DayOfWeek dayOfWeek) {
        Setting setting = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(null)
                .build();
        Space space = Space.builder().setting(setting).build();

        assertThat(space.isClosedOn(dayOfWeek)).isTrue();
    }
}
