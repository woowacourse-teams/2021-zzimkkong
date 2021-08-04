package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.TIME_UNIT_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class SettingsRequestTest extends RequestTest {
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 45, 70})
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
                .anyMatch(violation -> violation.getMessage().equals(TIME_UNIT_MESSAGE)))
                .isTrue();
    }
}
