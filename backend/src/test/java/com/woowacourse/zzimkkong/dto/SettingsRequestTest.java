package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DAY_OF_WEEK_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.TIME_UNIT_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class SettingsRequestTest extends RequestTest {
    @ParameterizedTest
    @ValueSource(ints = {1, 45, 70})
    @DisplayName("공간의 예약 설정에 단위 시간이 올바르지 않게 들어오면 처리한다.")
    void invalidTimeUnit(int timeUnit) {
        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                timeUnit,
                60,
                120,
                true,
                "Monday, Tuesday"
        );

        assertThat(getConstraintViolations(settingsRequest).stream()
                .noneMatch(violation -> violation.getMessage().equals(TIME_UNIT_MESSAGE)))
                .isFalse();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {5, 10, 30, 60, 120})
    @DisplayName("공간의 예약 설정에 단위 시간이 올바르게 들어온다.")
    void validTimeUnit(Integer timeUnit) {
        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                timeUnit,
                60,
                120,
                true,
                "Monday, Tuesday"
        );

        assertThat(getConstraintViolations(settingsRequest).stream()
                .noneMatch(violation -> violation.getMessage().equals(TIME_UNIT_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"Monday, Tuesday, Wednesday", "Monday,tuesday", "monday"})
    @DisplayName("공간의 예약 설정에 예약 가능 요일이 올바르지 않게 들어온다.")
    void validEnabledDayOfWeek(String days) {
        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                10,
                60,
                120,
                true,
                days
        );

        assertThat(getConstraintViolations(settingsRequest).stream()
                .noneMatch(violation -> violation.getMessage().equals(DAY_OF_WEEK_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"mon, tue", "monday, monday", "Tueday"})
    @DisplayName("공간의 예약 설정에 예약 가능 요일이 올바르게 들어온다.")
    void invalidEnabledDayOfWeek(String days) {
        SettingsRequest settingsRequest = new SettingsRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                10,
                60,
                120,
                true,
                days
        );

        assertThat(getConstraintViolations(settingsRequest).stream()
                .noneMatch(violation -> violation.getMessage().equals(DAY_OF_WEEK_MESSAGE)))
                .isFalse();
    }

}
