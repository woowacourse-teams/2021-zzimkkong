package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.exception.space.NoSuchDayOfWeekException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnabledDayOfWeekDtoTest {
    @Test
    @DisplayName("들어온 day of week input에 해당하는 요일의 필드는 true로 설정한 뒤 응답 객체를 생성한다")
    void from() {
        final EnabledDayOfWeekDto enabledDayOfWeekDto = EnabledDayOfWeekDto.from("monday, tuesday");
        assertThat(enabledDayOfWeekDto.getMonday()).isTrue();
        assertThat(enabledDayOfWeekDto.getTuesday()).isTrue();

        assertThat(enabledDayOfWeekDto.getWednesday()).isFalse();
        assertThat(enabledDayOfWeekDto.getThursday()).isFalse();
        assertThat(enabledDayOfWeekDto.getFriday()).isFalse();
        assertThat(enabledDayOfWeekDto.getSaturday()).isFalse();
        assertThat(enabledDayOfWeekDto.getSunday()).isFalse();
    }

    @Test
    @DisplayName("필드 중 true인 부분만 추출해서 ','으로 join한 string을 반환한다")
    void asString() {
        final EnabledDayOfWeekDto enabledDayOfWeekDto = EnabledDayOfWeekDto.from("monday,tuesday,wednesday");
        assertThat(enabledDayOfWeekDto.asString()).isEqualTo("monday,tuesday,wednesday");
    }
}
