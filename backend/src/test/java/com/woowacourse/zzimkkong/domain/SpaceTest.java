package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.CommonFixture.BE;
import static com.woowacourse.zzimkkong.CommonFixture.TOMORROW;
import static org.assertj.core.api.Assertions.assertThat;

public class SpaceTest {
    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 내에 있다면 false를 반환한다")
    void isNotBetweenAvailableTime() {
        LocalDateTime startDateTime = TOMORROW.atTime(10,0);
        LocalDateTime endDateTime = TOMORROW.atTime(18,0);
        boolean actual = BE.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 외에 있다면 true를 반환한다")
    void isNotBetweenAvailableTimeFail() {
        LocalDateTime startDateTime = TOMORROW.atTime(9,59);
        LocalDateTime endDateTime = TOMORROW.atTime(18,1);
        boolean actual = BE.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isTrue();
    }
}
