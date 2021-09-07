package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.space.ImpossibleAvailableStartEndTimeException;
import com.woowacourse.zzimkkong.exception.space.InvalidMinimumMaximumTimeUnitException;
import com.woowacourse.zzimkkong.exception.space.NotEnoughAvailableTimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SettingTest {
    @ParameterizedTest
    @CsvSource(value = {"10,9", "10,10"})
    @DisplayName("setting 생성 시 예약이 열릴 시간이 예약 닫힐 시간 이후거나 같으면 예외를 던진다")
    void invalidAvailableStartEndTime(int availableStartTimeHour, int availableEndTimeHour) {
        assertThatThrownBy(() -> Setting.builder()
                .availableStartTime(LocalTime.of(availableStartTimeHour, 0))
                .availableEndTime(LocalTime.of(availableEndTimeHour, 0))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build()).isInstanceOf(ImpossibleAvailableStartEndTimeException.class);
    }

    @Test
    @DisplayName("setting 생성 시 최대 예약 가능 시간이 최소 예약 가능시간 보다 작으면 예외를 던진다")
    void invalidMinimumMaximumTimeUnit() {
        Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();
        assertThatThrownBy(() -> Setting.builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(20)
                .reservationMaximumTimeUnit(10)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build()).isInstanceOf(InvalidMinimumMaximumTimeUnitException.class);
    }

    @Test
    @DisplayName("setting 생성 시 예약이 가능한 시간 범위가 최대 예약 가능 시간 보다 작으면 예외를 던진다")
    void notEnoughTimeAvailable() {
        assertThatThrownBy(() -> Setting.builder()
                .availableStartTime(LocalTime.of(10, 0))
                .availableEndTime(LocalTime.of(11, 0))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(70)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build()).isInstanceOf(NotEnoughAvailableTimeException.class);
    }
}
