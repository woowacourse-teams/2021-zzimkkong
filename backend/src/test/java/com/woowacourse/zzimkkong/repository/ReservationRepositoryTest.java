package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.woowacourse.zzimkkong.service.ReservationService.ONE_DAY;
import static org.assertj.core.api.Assertions.assertThat;

class ReservationRepositoryTest extends RepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private MapRepository mapRepository;

    private Space firstSpace;
    private Space secondSpace;
    private Map map;
    private Member member;
    private Reservation reservationFirstSpaceTargetDate0To1;
    private Reservation reservationFirstSpaceTargetDate13To14;
    private Reservation reservationFirstSpaceTargetDate18To23;
    private Reservation reservationFirstSpaceTheDayAfterTargetDate;
    private Reservation reservationSecondSpaceTargetDate0to1;
    private LocalDate targetDate;

    @BeforeEach
    void setUp() {
        member = new Member(
                "pobi@email.com",
                "test1234",
                "woowacourse");
        memberRepository.save(member);

        map = new Map("루터", member);
        mapRepository.save(map);

        firstSpace = new Space("회의실1", map);
        spaceRepository.save(firstSpace);

        secondSpace = new Space("회의실2", map);
        spaceRepository.save(secondSpace);

        targetDate = LocalDate.of(2021, 7, 9);

        reservationFirstSpaceTargetDate0To1 = new Reservation(
                targetDate.atStartOfDay(),
                targetDate.atTime(1, 0, 0),
                "찜꽁 1차 회의",
                "찜꽁",
                "1234",
                firstSpace
        );

        reservationFirstSpaceTargetDate13To14 = new Reservation(
                targetDate.atTime(13, 0, 0),
                targetDate.atTime(14, 0, 0),
                "찜꽁 2차 회의",
                "찜꽁",
                "1234",
                firstSpace
        );

        reservationFirstSpaceTargetDate18To23 = new Reservation(
                targetDate.atTime(18, 0, 0),
                targetDate.atTime(23, 59, 59),
                "찜꽁 3차 회의",
                "찜꽁",
                "6789",
                firstSpace
        );

        reservationFirstSpaceTheDayAfterTargetDate = new Reservation(
                targetDate.plusDays(1L).atStartOfDay(),
                targetDate.plusDays(1L).atTime(1, 0, 0),
                "찜꽁 4차 회의",
                "찜꽁",
                "1234",
                firstSpace
        );

        reservationSecondSpaceTargetDate0to1 = new Reservation(
                targetDate.atStartOfDay(),
                targetDate.atTime(1, 0, 0),
                "찜꽁 5차 회의",
                "찜꽁",
                "1234",
                secondSpace
        );

        reservationRepository.save(reservationFirstSpaceTargetDate0To1);
        reservationRepository.save(reservationFirstSpaceTargetDate13To14);
        reservationRepository.save(reservationFirstSpaceTargetDate18To23);
        reservationRepository.save(reservationFirstSpaceTheDayAfterTargetDate);
        reservationRepository.save(reservationSecondSpaceTargetDate0to1);
    }

    @DisplayName("space id와 특정 날짜가 주어질 때, 해당 날짜에 속하는 space의 예약들만 찾아온다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween() {
        // given
        LocalDateTime minimumDateTime = targetDate.atStartOfDay();
        LocalDateTime maximumDateTime = minimumDateTime.plusDays(1L);

        // when
        List<Reservation> reservations = reservationRepository.findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween(
                firstSpace.getId(),
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
                );

        // then
        assertThat(reservations).hasSize(3);
    }

    @DisplayName("space id 와 특정 날짜가 주어질 때, 조건에 부합하는 예약이 없으면 빈 리스트를 반환한다")
    @Test
    void findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween_noMatchingReservation() {
        // given
        LocalDateTime minimumDateTime = targetDate.plusDays(ONE_DAY).atStartOfDay();
        LocalDateTime maximumDateTime = minimumDateTime.plusDays(ONE_DAY);

        // when
        List<Reservation> reservations = reservationRepository.findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween(
                secondSpace.getId(),
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );

        // then
        assertThat(reservations).isEmpty();
    }
}
