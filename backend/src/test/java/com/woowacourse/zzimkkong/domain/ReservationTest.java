package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {
    private static final LocalDate DATE = LocalDate.of(2021, 7, 9);

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation.Builder()
                .startTime(DATE.atTime(8, 0, 0))
                .endTime(DATE.atTime(9, 0, 0))
                .build();
    }

    @DisplayName("겹치는 시간 정보가 주어지면 true, 예약 가능한 시간대면 false")
    @ParameterizedTest
    @MethodSource("provideStartAndEndDateTime")
    void hasConflictWith(LocalDateTime startTime, LocalDateTime endTime, Boolean result) {
        assertThat(reservation.hasConflictWith(startTime, endTime)).isEqualTo(result);
    }

    private static Stream<Arguments> provideStartAndEndDateTime() {
        return Stream.of(
                Arguments.of(
                        DATE.atTime(8,1,0),
                        DATE.atTime(8,59,0),
                        true),
                Arguments.of(
                        DATE.atTime(7,59,0),
                        DATE.atTime(8,1,0),
                        true),
                Arguments.of(
                        DATE.atTime(8,59,0),
                        DATE.atTime(9,1,0),
                        true),
                Arguments.of(
                        DATE.atTime(7,59,0),
                        DATE.atTime(9,1,0),
                        true),
                Arguments.of(
                        DATE.atTime(8,0,0),
                        DATE.atTime(9,0,0),
                        true),
                Arguments.of(
                        DATE.atTime(7,59,0),
                        DATE.atTime(8,0,0),
                        false),
                Arguments.of(
                        DATE.atTime(9,0,0),
                        DATE.atTime(9,1,0),
                        false),
                Arguments.of(
                        DATE.atTime(7,0,0),
                        DATE.atTime(8,0,0),
                        false),
                Arguments.of(
                        DATE.atTime(9,0,0),
                        DATE.atTime(10,0,0),
                        false)
        );
    }
}
