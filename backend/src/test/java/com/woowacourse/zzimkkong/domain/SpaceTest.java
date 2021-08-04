package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SpaceTest {
    private Space be;
    @BeforeEach
    void setUp() {
        Setting beSetting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .disabledWeekdays(BE_ENABLED_DAY_OF_WEEK)
                .build();

        be = new Space.Builder()
                .setting(beSetting)
                .build();
    }

    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 내에 있다면 false를 반환한다")
    void isNotBetweenAvailableTime() {
        LocalDateTime startDateTime = THE_DAY_AFTER_TOMORROW.atTime(10,0);
        LocalDateTime endDateTime = THE_DAY_AFTER_TOMORROW.atTime(18,0);
        boolean actual = be.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("예약하려는 시간이 공간의 예약 가능한 시간 외에 있다면 true를 반환한다")
    void isNotBetweenAvailableTimeFail() {
        LocalDateTime startDateTime = THE_DAY_AFTER_TOMORROW.atTime(9,59);
        LocalDateTime endDateTime = THE_DAY_AFTER_TOMORROW.atTime(18,1);
        boolean actual = be.isNotBetweenAvailableTime(startDateTime, endDateTime);

        assertThat(actual).isTrue();
    }

    //TODO 예약시간단위 도메인테스트 추가
}
