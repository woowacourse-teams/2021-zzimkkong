package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.woowacourse.zzimkkong.Constants.THE_DAY_AFTER_TOMORROW;
import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder()
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                THE_DAY_AFTER_TOMORROW.atTime(8, 0),
                                THE_DAY_AFTER_TOMORROW.atTime(9, 0)))
                .password("1234")
                .build();
    }

    @Test
    @DisplayName("예약 비밀번호가 잘못되었으면 true, 정확하면 false")
    void isWrongPassword() {
        assertThat(reservation.isWrongPassword("1321")).isTrue();
        assertThat(reservation.isWrongPassword("1234")).isFalse();
    }
}
