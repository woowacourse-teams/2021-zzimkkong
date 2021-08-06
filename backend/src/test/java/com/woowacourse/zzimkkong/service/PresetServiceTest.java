package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.member.PresetCreateResponse;
import com.woowacourse.zzimkkong.dto.member.PresetFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.repository.PresetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    private Member pobi;
    private Setting setting;
    private Preset firstPreset;
    private Preset secondPreset;

    @BeforeEach
    void setUp() {
        pobi = new Member(1L, EMAIL, PASSWORD, ORGANIZATION);

        setting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        firstPreset = new Preset(1L, setting, pobi);
        secondPreset = new Preset(2L, setting, pobi);
    }

    @Test
    @DisplayName("프리셋 저장 요청 시, 프리셋을 저장한다.")
    void save() {
        //given
        given(presets.save(any(Preset.class)))
                .willReturn(firstPreset);

        //when
        PresetCreateResponse presetCreateResponse = presetService.savePreset(settingsRequest, pobi);

        //then
        assertThat(presetCreateResponse.getId()).isNotNull();
    }

    @Test
    @DisplayName("모든 프리셋 조회 요청 시, member가 가진 모든 프리셋을 조회한다.")
    void findAll() {
        //given
        List<Preset> expectedPresets = List.of(firstPreset, secondPreset);
        given(presets.findAllByMember(any(Member.class)))
                .willReturn(expectedPresets);

        //when
        PresetFindAllResponse presetFindAllResponse = presetService.findAllPresets(pobi);

        //then
        assertThat(presetFindAllResponse).usingRecursiveComparison()
                .isEqualTo(PresetFindAllResponse.from(expectedPresets));

    }
}
