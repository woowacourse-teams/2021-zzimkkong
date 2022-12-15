package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {
    @Test
    @DisplayName("자신의 preset 중에 id가 같은 것을 반환한다.")
    void findPresetById() {
        //given, when
        Member member = Member.builder()
                .id(1L)
                .email(EMAIL)
                .userName(USER_NAME)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        Preset preset = Preset.builder()
                .id(1L)
                .name(PRESET_NAME1)
                .settingTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .member(member)
                .build();

        //then
        assertThat(member.findPresetById(1L))
                .isEqualTo(Optional.of(preset));
    }

    @ParameterizedTest
    @DisplayName("멤버 객체는 이메일을 입력받아 자신의 이메일 정보와 비교할 수 있다.")
    @CsvSource(value = {"pobi@email.com, true", "wrongemail@woowa.com, false"}, delimiter = ',')
    void isSameEmail(String email, boolean expected) {
        // given
        Member member = Member.builder()
                .id(1L)
                .email(EMAIL)
                .userName(USER_NAME)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();

        // when
        boolean actual = member.isSameEmail(email);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
