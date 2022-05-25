package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.TimeSlot;
import com.woowacourse.zzimkkong.dto.member.PresetCreateRequest;
import com.woowacourse.zzimkkong.dto.member.PresetCreateResponse;
import com.woowacourse.zzimkkong.dto.member.PresetFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.exception.preset.NoSuchPresetException;
import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
import com.woowacourse.zzimkkong.repository.PresetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class PresetServiceTest extends ServiceTest {
    @Autowired
    private PresetService presetService;

    @MockBean
    private PresetRepository presets;

    private final SettingsRequest settingsRequest = new SettingsRequest(
            BE_AVAILABLE_START_TIME,
            BE_AVAILABLE_END_TIME,
            BE_RESERVATION_TIME_UNIT.getMinutes(),
            BE_RESERVATION_MINIMUM_TIME_UNIT.getMinutes(),
            BE_RESERVATION_MAXIMUM_TIME_UNIT.getMinutes(),
            BE_RESERVATION_ENABLE,
            EnabledDayOfWeekDto.from(BE_ENABLED_DAY_OF_WEEK)
    );

    private final Setting setting = Setting.builder()
            .availableTimeSlot(TimeSlot.of(
                    BE_AVAILABLE_START_TIME,
                    BE_AVAILABLE_END_TIME))
            .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
            .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
            .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
            .reservationEnable(BE_RESERVATION_ENABLE)
            .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
            .build();

    private Member pobi;
    private LoginEmailDto pobiEmail;

    @BeforeEach
    void setUp() {
        pobi = new Member(1L, EMAIL, PW, ORGANIZATION);
        pobiEmail = LoginEmailDto.from(EMAIL);
    }

    @Test
    @DisplayName("프리셋 저장 요청 시, 프리셋을 저장한다.")
    void save() {
        //given
        PresetCreateRequest presetCreateRequest = new PresetCreateRequest(PRESET_NAME1, settingsRequest);
        Preset expected = new Preset(1L, presetCreateRequest.getName(), setting, pobi);

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(presets.save(any(Preset.class)))
                .willReturn(expected);

        //when
        PresetCreateResponse presetCreateResponse = presetService.savePreset(presetCreateRequest, pobiEmail);

        //then
        assertThat(presetCreateResponse.getId()).isNotNull();
    }

    @Test
    @DisplayName("모든 프리셋 조회 요청 시, member가 가진 모든 프리셋을 조회한다.")
    void findAll() {
        //given
        Preset firstPreset = new Preset(1L, PRESET_NAME1, setting, pobi);
        Preset secondPreset = new Preset(2L, PRESET_NAME2, setting, pobi);

        List<Preset> expectedPresets = List.of(firstPreset, secondPreset);
        given(members.findByEmailWithFetchPresets(anyString()))
                .willReturn(Optional.of(pobi));

        //when
        PresetFindAllResponse presetFindAllResponse = presetService.findAllPresets(pobiEmail);

        //then
        assertThat(presetFindAllResponse).usingRecursiveComparison()
                .isEqualTo(PresetFindAllResponse.from(expectedPresets));
    }

    @Test
    @DisplayName("프리셋 삭제 요청 시, 프리셋을 삭제한다.")
    void delete() {
        //given
        Preset savedPreset = new Preset(1L, PRESET_NAME1, setting, pobi);
        given(members.findByEmailWithFetchPresets(anyString()))
                .willReturn(Optional.of(pobi));

        //when, then
        assertDoesNotThrow(() -> presetService.deletePreset(savedPreset.getId(), pobiEmail));
    }

    @Test
    @DisplayName("프리셋 삭제 요청 시, 사용자의 프리셋이 아니면 예외가 발생한다.")
    void deleteOwnerException() {
        //given
        Preset savedPreset = new Preset(1L, PRESET_NAME1, setting, pobi);
        LoginEmailDto anotherEmail = LoginEmailDto.from(NEW_EMAIL);
        Member anotherMember = new Member(NEW_EMAIL, PW, ORGANIZATION);

        given(members.findByEmailWithFetchPresets(anyString()))
                .willReturn(Optional.of(anotherMember));
        Long savedPresetId = savedPreset.getId();

        //when, then
        assertThatThrownBy(() -> presetService.deletePreset(savedPresetId, anotherEmail)).isInstanceOf(NoSuchPresetException.class);
    }

    @Test
    @DisplayName("프리셋 삭제 요청 시, 프리셋이 존재하지 않으면 예외가 발생한다.")
    void deleteInvalidPresetException() {
        //given, when
        given(members.findByEmailWithFetchPresets(anyString()))
                .willReturn(Optional.of(pobi));

        //then
        assertThatThrownBy(() -> presetService.deletePreset(1L, pobiEmail))
                .isInstanceOf(NoSuchPresetException.class);
    }
}
