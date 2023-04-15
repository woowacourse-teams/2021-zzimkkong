package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.dto.space.SettingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.TIME_UNIT_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class SettingRequestTest extends RequestTest {
    @ParameterizedTest
    @ValueSource(ints = {1, 45, 70})
    @DisplayName("공간의 예약 설정에 단위 시간이 올바르지 않게 들어오면 처리한다.")
    void invalidTimeUnit(int timeUnit) {
        SettingRequest settingRequest = new SettingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                timeUnit,
                60,
                120,
                EnabledDayOfWeekDto.from("Monday, Tuesday"),
                0
        );

        assertThat(getConstraintViolations(settingRequest).stream()
                .noneMatch(violation -> violation.getMessage().equals(TIME_UNIT_MESSAGE)))
                .isFalse();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {5, 10, 30, 60})
    @DisplayName("공간의 예약 설정에 단위 시간이 올바르게 들어온다.")
    void validTimeUnit(Integer timeUnit) {
        SettingRequest settingRequest = new SettingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                timeUnit,
                60,
                120,
                EnabledDayOfWeekDto.from("Monday, Tuesday"),
                0
        );

        assertThat(getConstraintViolations(settingRequest).stream()
                .noneMatch(violation -> violation.getMessage().equals(TIME_UNIT_MESSAGE)))
                .isTrue();
    }
}
