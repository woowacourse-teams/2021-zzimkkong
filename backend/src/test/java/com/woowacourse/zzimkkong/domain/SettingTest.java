package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.space.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SettingTest {
    @Test
    @DisplayName("setting의 입력값이 모두 올바르면 setting을 생성한다")
    void name() {
        assertDoesNotThrow(() -> Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build());
    }

    @ParameterizedTest
    @CsvSource(value = {"10,9", "10,10"})
    @DisplayName("setting 생성 시 예약이 열릴 시간이 예약 닫힐 시간 이후거나 같으면 예외를 던진다")
    void invalidAvailableStartEndTime(int availableStartTimeHour, int availableEndTimeHour) {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableStartTime(LocalTime.of(availableStartTimeHour, 0))
                .availableEndTime(LocalTime.of(availableEndTimeHour, 0))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(ImpossibleAvailableStartEndTimeException.class);
    }

    @Test
    @DisplayName("setting 생성 시 최대 예약 가능 시간이 최소 예약 가능시간 보다 작으면 예외를 던진다")
    void invalidMinimumMaximumTimeUnit() {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(20)
                .reservationMaximumTimeUnit(10)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(InvalidMinimumMaximumTimeUnitException.class);
    }

    @Test
    @DisplayName("setting 생성 시 예약이 가능한 시간 범위가 최대 예약 가능 시간 보다 작으면 예외를 던진다")
    void notEnoughTimeAvailable() {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(11, 0))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(70)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(NotEnoughAvailableTimeException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"35,55,10", "4,24,10", "17,22,5", "57,2,5", "50,20,30", "20,20,60"})
    @DisplayName("setting 생성 시 예약이 시작되는 시간과 닫히는 시간이 time unit단위와 맞으면 예외를 던지지 않는다")
    void timeUnitMismatch_ok(int startMinute, int endMinute, int timeUnit) {
        assertDoesNotThrow(() -> Setting.builder()
                .availableStartTime(LocalTime.of(10, startMinute))
                .availableEndTime(LocalTime.of(20, endMinute))
                .reservationTimeUnit(timeUnit)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build());
    }

    @ParameterizedTest
    @CsvSource(value = {"26,27,5", "15,0,10", "0,25,30", "10,20,60"})
    @DisplayName("setting 생성 시 예약이 시작되는 시간과 닫히는 시간이 time unit단위와 맞지 않으면 예외를 던진다")
    void timeUnitMismatch_fail(int startMinute, int endMinute, int timeUnit) {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableStartTime(LocalTime.of(10, startMinute))
                .availableEndTime(LocalTime.of(20, endMinute))
                .reservationTimeUnit(timeUnit)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(TimeUnitMismatchException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"0,5", "5,10", "9,20", "10,25", "15,30", "25,45"})
    @DisplayName("setting 생성 시 최소,최대 예약 가능 시간의 단위가 예약 시간 단위와 일치하지 않으면 예외를 던진다")
    void timeUnitInconsistency_fail(int minimumMinute, int maximumMinute) {
        final Setting.SettingBuilder settingBuilder = Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(minimumMinute)
                .reservationMaximumTimeUnit(maximumMinute)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK);
        assertThatThrownBy(settingBuilder::build).isInstanceOf(TimeUnitInconsistencyException.class);
    }
}
