package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.member.PresetCreateRequest;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;
import static org.assertj.core.api.Assertions.assertThat;

class PresetCreateRequestTest extends RequestTest {
    private final SettingsRequest settingsRequest = new SettingsRequest(
            BE_AVAILABLE_START_TIME,
            BE_AVAILABLE_END_TIME,
            BE_RESERVATION_TIME_UNIT,
            BE_RESERVATION_MINIMUM_TIME_UNIT,
            BE_RESERVATION_MAXIMUM_TIME_UNIT,
            BE_RESERVATION_ENABLE,
            EnabledDayOfWeekDto.from(BE_ENABLED_DAY_OF_WEEK)
    );

    @ParameterizedTest
    @NullSource
    @DisplayName("프리셋 생성에 빈 이름이 들어오면 처리한다.")
    void blankName(String name) {
        PresetCreateRequest nameRequest = new PresetCreateRequest(name, settingsRequest);

        assertThat(getConstraintViolations(nameRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"공백 입력 가능!:false", "옳지않은프리셋옳지않은프리셋옳지않은프리셋:true", "옳은프리셋!:false"}, delimiter = ':')
    @DisplayName("예약 생성의 이름에 옳지 않은 형식의 문자열이 들어오면 처리한다.")
    void invalidName(String name, boolean flag) {
        PresetCreateRequest nameRequest = new PresetCreateRequest(name, settingsRequest);

        assertThat(getConstraintViolations(nameRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(PRESET_NAME_MESSAGE)))
                .isEqualTo(flag);
    }
}
