package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.CommonFixture.FE1_ZERO_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReservationRepositoryTest extends RepositoryTest {
    @BeforeEach
    void setUp() {
        members.save(POBI);
        maps.save(LUTHER);
        spaces.save(BE);
        spaces.save(FE1);

        reservations.save(BE_AM_ZERO_ONE);
        reservations.save(BE_PM_ONE_TWO);
        reservations.save(BE_NEXT_DAY_PM_SIX_TWELVE);
        reservations.save(FE1_ZERO_ONE);
    }

    @Test
    @DisplayName("예약을 추가할 수 있다.")
    void save() {
        //given
        Reservation be_two_three = new Reservation.Builder()
                .startTime(TOMORROW_START_TIME.plusHours(2))
                .endTime(TOMORROW_START_TIME.plusHours(3))
                .description("찜꽁 4차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(BE)
                .build();

        //when
        final Reservation actual = reservations.save(be_two_three);

        //then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual).isEqualTo(be_two_three);
    }

    @DisplayName("map id, space id, 특정 시간이 주어질 때, 해당 spaceId와 해당 시간에 속하는 예약들만 찾아온다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween() {
        // given, when
        List<Reservation> foundReservations = getReservations(
                List.of(BE.getId()),
                TOMORROW_START_TIME,
                TOMORROW_START_TIME.plusHours(14));

        // then
        assertThat(foundReservations).containsExactly(BE_AM_ZERO_ONE, BE_PM_ONE_TWO);
    }

    @DisplayName("특정 시간에 부합하는 예약이 없으면 빈 리스트를 반환한다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_noMatchingTime() {
        // given, when
        List<Reservation> foundReservations = getReservations(
                List.of(BE.getId()),
                TOMORROW_START_TIME.plusHours(15),
                TOMORROW_START_TIME.plusHours(18));

        // then
        assertThat(foundReservations).isEmpty();
    }

    @DisplayName("특정 공간에 부합하는 예약이 없으면 빈 리스트를 반환한다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_noMatchingReservation() {
        // given, when
        List<Reservation> foundReservations = getReservations(
                List.of(FE1.getId()),
                TOMORROW_START_TIME.plusHours(13),
                TOMORROW_START_TIME.plusHours(14));

        // then
        assertThat(foundReservations).isEmpty();
    }

    @DisplayName("map id와 특정 날짜가 주어질 때, 해당 날짜에 속하는 해당 map의 모든 space들의 예약들을 찾아온다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_allSpaces() {
        // given, when
        List<Reservation> foundReservations = getReservations(
                List.of(BE.getId(), FE1.getId()),
                TOMORROW_START_TIME,
                TOMORROW_START_TIME.plusDays(1));

        // then
        assertThat(foundReservations).containsExactly(BE_AM_ZERO_ONE, BE_PM_ONE_TWO, FE1_ZERO_ONE);
    }

    @Test
    @DisplayName("예약을 삭제할 수 있다.")
    void delete() {
        //given, when, then
        assertDoesNotThrow(() -> reservations.delete(BE_NEXT_DAY_PM_SIX_TWELVE));
    }

    private List<Reservation> getReservations(List<Long> spaceIds, LocalDateTime minimumDateTime, LocalDateTime maximumDateTime) {
        return reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                spaceIds,
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );
    }
}
