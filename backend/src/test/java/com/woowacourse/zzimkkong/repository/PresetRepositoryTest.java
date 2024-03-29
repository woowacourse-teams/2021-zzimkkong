package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.domain.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class PresetRepositoryTest extends RepositoryTest {
    @Autowired
    private PresetRepository presets;

    private Preset preset;

    @BeforeEach
    void setUp() {
        Member pobi = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        members.save(pobi);

        preset = Preset.builder()
                .name(PRESET_NAME1)
                .settingTimeSlot(
                        TimeSlot.of(
                                BE_AVAILABLE_START_TIME,
                                BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .member(pobi)
                .build();
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
}
