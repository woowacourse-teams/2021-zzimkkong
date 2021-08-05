package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MapTest {
    @Test
    @DisplayName("맵의 관리자가 아니면 true, 맞으면 false")
    void isNotOwnedBy() {
        Member pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);

        boolean result = luther.isNotOwnedBy(new Member("삭정이", "test1234", "잠실"));
        assertThat(result).isTrue();
    }
}
