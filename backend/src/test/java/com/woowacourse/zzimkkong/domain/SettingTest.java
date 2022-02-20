package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.ImpossibleStartEndTimeException;
import com.woowacourse.zzimkkong.exception.space.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SettingTest {
    @Test
    @DisplayName("setting의 입력값이 모두 올바르면 setting을 생성한다")
    void name() {
        assertDoesNotThrow(() -> Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build());
    }

    @Test
    @DisplayName("setting 생성 시 최대 예약 가능 시간이 최소 예약 가능시간 보다 작으면 예외를 던진다")
    void invalidMinimumMaximumTimeUnit() {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(Minute.from(20))
                .reservationMaximumTimeUnit(Minute.from(10))
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(InvalidMinimumMaximumTimeUnitException.class);
    }

    @Test
    @DisplayName("setting 생성 시 예약이 가능한 시간 범위가 최대 예약 가능 시간 보다 작으면 예외를 던진다")
    void notEnoughTimeAvailable() {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0)))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(Minute.from(70))
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(NotEnoughAvailableTimeException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"30,50,10", "0,0,10", "10,15,5", "0,5,5", "0,30,30", "0,0,60"})
    @DisplayName("setting 생성 시 예약이 시작되는 시간과 닫히는 시간이 time unit단위와 맞으면 예외를 던지지 않는다")
    void timeUnitMismatch_ok(int startMinute, int endMinute, int timeUnit) {
        assertDoesNotThrow(() -> Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        LocalTime.of(10, startMinute),
                        LocalTime.of(20, endMinute)))
                .reservationTimeUnit(Minute.from(timeUnit))
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build());
    }

    @ParameterizedTest
    @CsvSource(value = {"0,15,10", "15,0,10", "10,40,30", "5,5,60"})
    @DisplayName("setting 생성 시 예약이 시작되는 시간과 닫히는 시간이 time unit단위와 맞지 않으면 예외를 던진다")
    void timeUnitMismatch_fail(int startMinute, int endMinute, int timeUnit) {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        LocalTime.of(10, startMinute),
                        LocalTime.of(20, endMinute)))
                .reservationTimeUnit(Minute.from(timeUnit))
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(TimeUnitMismatchException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"0,5", "5,10", "15,20", "10,25", "15,30", "25,45"})
    @DisplayName("setting 생성 시 최소,최대 예약 가능 시간의 단위가 예약 시간 단위와 일치하지 않으면 예외를 던진다")
    void timeUnitInconsistency_fail(int minimumMinute, int maximumMinute) {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(Minute.from(10))
                .reservationMinimumTimeUnit(Minute.from(minimumMinute))
                .reservationMaximumTimeUnit(Minute.from(maximumMinute))
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(TimeUnitInconsistencyException.class);
    }
}
