package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekResponse;
import com.woowacourse.zzimkkong.exception.dto.EnabledDayOfWeekResponseSetException;
import com.woowacourse.zzimkkong.exception.space.NoSuchDayOfWeekException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class EnabledDayOfWeekResponseTest {

    @Test
    @DisplayName("들어온 day of week input에 해당하는 요일의 필드는 true로 설정한 뒤 응답 객체를 생성한다")
    void from() {
        final EnabledDayOfWeekResponse enabledDayOfWeekResponse = EnabledDayOfWeekResponse.from("monday, tuesday");
        assertThat(enabledDayOfWeekResponse.getMonday()).isTrue();
        assertThat(enabledDayOfWeekResponse.getTuesday()).isTrue();

        assertThat(enabledDayOfWeekResponse.getWednesday()).isFalse();
        assertThat(enabledDayOfWeekResponse.getThursday()).isFalse();
        assertThat(enabledDayOfWeekResponse.getFriday()).isFalse();
        assertThat(enabledDayOfWeekResponse.getSaturday()).isFalse();
        assertThat(enabledDayOfWeekResponse.getSunday()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"monday, tuesdayy", "wednesday, thursdaayyyy"})
    @DisplayName("들어온 input 에 잘못된 day of week 형태가 있으면 예외를 반환한다")
    void from_wrongDayOfWeek(String dayOfWeek) {
        assertThatThrownBy(() -> EnabledDayOfWeekResponse.from(dayOfWeek))
                .isInstanceOf(NoSuchDayOfWeekException.class);
    }
}
