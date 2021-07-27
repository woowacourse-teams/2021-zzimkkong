package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class MapTest {
    @DisplayName("맵의 주인이 일치하면 false, 불일치하면 true를 반환한다.")
    @Test
    void checkOwnedBy() {
        //given, when, then
        assertThat(LUTHER.isNotOwnedBy(POBI)).isFalse();
        assertThat(LUTHER.isNotOwnedBy(JASON)).isTrue();
    }
}
