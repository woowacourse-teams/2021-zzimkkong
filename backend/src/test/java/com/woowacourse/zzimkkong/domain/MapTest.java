package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.woowacourse.zzimkkong.CommonFixture.LUTHER;
import static org.assertj.core.api.Assertions.assertThat;

public class MapTest {
    @DisplayName("맵의 관리자가 아니면 true, 맞으면 false")
    @Test
    void isNotOwnedBy() {
        boolean result = LUTHER.isNotOwnedBy(new Member("삭정이", "test1234", "잠실"));
        assertThat(result).isTrue();
    }
}
