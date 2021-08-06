package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class PresetRepositoryTest extends RepositoryTest {
    @Autowired
    private PresetRepository presets;

    private Member pobi;
    private Preset preset;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        members.save(pobi);

        Setting setting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        preset = new Preset(setting, pobi);
    }

    @Test
    @DisplayName("프리셋을 저장한다.")
    void save() {
        //given, when
        Preset savedPreset = presets.save(preset);

        //then
        assertThat(savedPreset.getId()).isNotNull();
        assertThat(savedPreset).isEqualTo(preset);
    }

    @Test
    @DisplayName("멤버의 모든 프리셋을 저장할 수 있다.")
    void findAllByMember() {
        //given
        Preset savedPreset = presets.save(preset);

        //when
        List<Preset> actualPresets = presets.findAllByMember(pobi);

        //then
        assertThat(actualPresets).containsExactlyInAnyOrderElementsOf(List.of(savedPreset));
    }
}
