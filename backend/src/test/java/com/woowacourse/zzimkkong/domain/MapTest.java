package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

class MapTest {
    @Test
    @DisplayName("Space가 생성되면 Map에 Space를 추가한다")
    void addSpace() {
        Member pobi = new Member(EMAIL, PW, ORGANIZATION);
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);

        assertThat(luther.getSpaces().size()).isZero();
        Space.builder()
                .map(luther)
                .build();
        assertThat(luther.getSpaces().size()).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({"pobi@email.com,true", "sakjung@email.com,false"})
    @DisplayName("맵의 관리자가 맞으면 true, 아니면 false")
    void isOwnedBy(String email, boolean expected) {
        Member pobi = new Member(EMAIL, PW, ORGANIZATION);
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);

        boolean result = luther.isOwnedBy(email);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("생성자 인자에 주어지는 Member가 null이 아니라면 Member의 maps에 Map이 추가된다.")
    @CsvSource({"true", "false"})
    void addMap(boolean nullable) {
        Map luther;
        if (nullable) {
            luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, null);
            assertThat(luther.getMember()).isNull();
            return;
        }
        Member pobi = new Member(EMAIL, PW, ORGANIZATION);
        luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);

        assertThat(pobi.getMaps()).contains(luther);
    }
}
