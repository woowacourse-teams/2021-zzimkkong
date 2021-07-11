package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.exception.NoSuchSpaceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.woowacourse.zzimkkong.service.ReservationService.ONE_DAY;
import static org.assertj.core.api.Assertions.assertThat;

class ReservationRepositoryTest extends RepositoryTest {
    private final LocalDate targetDate = LocalDate.of(2021, 7, 9);
    private Space be;
    private Space fe1;

    private static final LocalDateTime startTime = LocalDateTime.of(2021, 5, 6, 16, 23, 0);
    private static final LocalDateTime endTime = LocalDateTime.of(2021, 5, 6, 19, 23, 0);
    public static final Space SPACE = new Space("회의실", MapRepositoryTest.MAP);
    public static final Reservation RESERVATION = new Reservation.Builder()
            .startTime(startTime)
            .endTime(endTime)
            .description("찜꽁 3차 회의")
            .userName("찜꽁")
            .password("1234")
            .space(SPACE)
            .build();

    @Autowired
    private ReservationRepository reservations;

    @BeforeEach
    void setUp() {
        super.setUp();

        be = spaceRepository.findById(1L).orElseThrow(NoSuchSpaceException::new);
        fe1 = spaceRepository.findById(2L).orElseThrow(NoSuchSpaceException::new);
    }

    @DisplayName("map id, space id, 특정 날짜가 주어질 때, 해당 spaceId와 해당 날짜에 속하는 예약들만 찾아온다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween() {
        // given
        LocalDateTime minimumDateTime = targetDate.atStartOfDay();
        LocalDateTime maximumDateTime = minimumDateTime.plusDays(ONE_DAY);

        // when
        List<Reservation> reservations = reservationRepository.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                Collections.singletonList(be.getId()),
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );

        // then
        assertThat(reservations).hasSize(3);
    }

    @DisplayName("map id, space id, 특정 날짜가 주어질 때, 조건에 부합하는 예약이 없으면 빈 리스트를 반환한다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_noMatchingReservation() {
        // given
        LocalDateTime minimumDateTime = targetDate.plusDays(ONE_DAY).atStartOfDay();
        LocalDateTime maximumDateTime = minimumDateTime.plusDays(ONE_DAY);

        // when
        List<Reservation> reservations = reservationRepository.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                Collections.singletonList(fe1.getId()),
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );

        // then
        assertThat(reservations).isEmpty();
    }

    @DisplayName("map id와 특정 날짜가 주어질 때, 해당 날짜에 속하는 해당 map의 모든 space들의 예약들을 찾아온다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_allSpaces() {
        LocalDateTime minimumDateTime = targetDate.atStartOfDay();
        LocalDateTime maximumDateTime = minimumDateTime.plusDays(ONE_DAY);

        // when
        List<Reservation> reservations = reservationRepository.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                Arrays.asList(be.getId(), fe1.getId()),
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );

        // then
        assertThat(reservations).hasSize(4);
    }

    @Test
    @DisplayName("예약을 추가할 수 있다.")
    void save() {
        //when
        final Reservation actual = reservations.save(RESERVATION);

        //then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getPassword()).isEqualTo("1234");
    }

    @Test
    @DisplayName("예약을 삭제할 수 있다.")
    void delete() {
        //given
        final Reservation reservation = reservations.save(RESERVATION);

        //when
        reservations.delete(reservation);

        //then
        reservations.flush();
    }
}
