package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.woowacourse.zzimkkong.Constants.THE_DAY_AFTER_TOMORROW;
import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder()
                .startTime(THE_DAY_AFTER_TOMORROW.atTime(8, 0))
                .endTime(THE_DAY_AFTER_TOMORROW.atTime(9, 0))
                .build();
    }

    @ParameterizedTest
    @CsvSource(value = {"08:01+08:59+true", "07:59+08:01+true", "08:59+09:01+true",
            "07:59+09:01+true", "08:00+09:00+true", "07:59+08:00+false",
            "09:00+09:01+false", "07:00+08:00+false", "09:00+10:00+false"}, delimiter = '+')
    @DisplayName("겹치는 시간 정보가 주어지면 true, 예약 가능한 시간대면 false")
    void hasConflictWith(String startTime, String endTime, Boolean result) {
        LocalDateTime start = THE_DAY_AFTER_TOMORROW.atTime(LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm")));
        LocalDateTime end = THE_DAY_AFTER_TOMORROW.atTime(LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm")));
        assertThat(reservation.hasConflictWith(start, end)).isEqualTo(result);
    }
}
