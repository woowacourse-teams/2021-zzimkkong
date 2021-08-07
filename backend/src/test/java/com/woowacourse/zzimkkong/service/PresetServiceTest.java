package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.PresetCreateRequest;
import com.woowacourse.zzimkkong.dto.member.PresetCreateResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.exception.preset.NoAuthorityOnPresetException;
import com.woowacourse.zzimkkong.exception.preset.NoSuchPresetException;
import com.woowacourse.zzimkkong.repository.PresetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class PresetServiceTest extends ServiceTest {
    @Autowired
    private PresetService presetService;

    @MockBean
    private PresetRepository presets;

    private final SettingsRequest settingsRequest = new SettingsRequest(
            BE_AVAILABLE_START_TIME,
            BE_AVAILABLE_END_TIME,
            BE_RESERVATION_TIME_UNIT,
            BE_RESERVATION_MINIMUM_TIME_UNIT,
            BE_RESERVATION_MAXIMUM_TIME_UNIT,
            BE_RESERVATION_ENABLE,
            BE_ENABLED_DAY_OF_WEEK
    );

    private final Setting setting = new Setting.Builder()
            .availableStartTime(BE_AVAILABLE_START_TIME)
            .availableEndTime(BE_AVAILABLE_END_TIME)
            .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
            .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
            .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
            .reservationEnable(BE_RESERVATION_ENABLE)
            .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
            .build();

    private Member pobi;

    @BeforeEach
    void setUp() {
        pobi = new Member(1L, EMAIL, PASSWORD, ORGANIZATION);
    }

    @Test
    @DisplayName("프리셋 저장 요청 시, 프리셋을 저장한다.")
    void save() {
        //given
        PresetCreateRequest presetCreateRequest = new PresetCreateRequest(PRESET_NAME1, settingsRequest);

        Preset expected = new Preset(1L, presetCreateRequest.getName(), setting, pobi);

        given(presets.save(any(Preset.class)))
                .willReturn(expected);

        //when
        PresetCreateResponse presetCreateResponse = presetService.savePreset(presetCreateRequest, pobi);

        //then
        assertThat(presetCreateResponse.getId()).isNotNull();
    }

    @Test
    @DisplayName("프리셋 삭제 요청 시, 프리셋을 삭제한다.")
    void delete() {
        //given
        Preset savedPreset = new Preset(1L, PRESET_NAME1, setting, pobi);

        given(presets.findById(anyLong()))
                .willReturn(Optional.of(savedPreset));

        //when, then
        assertDoesNotThrow(() -> presetService.deletePreset(savedPreset.getId(), pobi));
    }

    @Test
    @DisplayName("프리셋 삭제 요청 시, 사용자의 프리셋이 아니면 예외가 발생한다.")
    void deleteOwnerException() {
        //given
        Preset savedPreset = new Preset(1L, PRESET_NAME1, setting, pobi);

        given(presets.findById(anyLong()))
                .willReturn(Optional.of(savedPreset));

        Member jason = new Member(2L, "jason@email.com", PASSWORD, ORGANIZATION);

        //when, then
        assertThatThrownBy(() -> presetService.deletePreset(savedPreset.getId(), jason))
                .isInstanceOf(NoAuthorityOnPresetException.class);
    }

    @Test
    @DisplayName("프리셋 삭제 요청 시, 프리셋이 존재하지 않으면 예외가 발생한다.")
    void deleteInvalidPresetException() {
        //given
        Preset savedPreset = new Preset(1L, PRESET_NAME1, setting, pobi);

        given(presets.findById(anyLong()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> presetService.deletePreset(savedPreset.getId(), pobi))
                .isInstanceOf(NoSuchPresetException.class);
    }
}
