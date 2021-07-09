package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    private Space space1;
    private Map map;
    private Member member;
    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;
    private Reservation reservation4;

    @BeforeEach
    void setUp() {
        member = new Member(
                "pobi@email.com",
                "test1234",
                "woowacourse");
        memberRepository.save(member);

        map = new Map("루터", member);
        mapRepository.save(map);

        space1 = new Space("회의실1", map);
        spaceRepository.save(space1);

        reservation1 = new Reservation(
                LocalDateTime.of(
                        LocalDate.of(2021, 7, 9),
                        LocalTime.of(0, 0, 0, 0)
                ),
                LocalDateTime.of(
                        LocalDate.of(2021, 7, 9),
                        LocalTime.of(1, 0, 0, 0)
                ),
                "1234",
                "찜꽁",
                "찜꽁 1차 회의",
                space1
        );

        reservation2 = new Reservation(
                LocalDateTime.of(
                        LocalDate.of(2021, 7, 9),
                        LocalTime.of(13, 0, 0, 0)
                ),
                LocalDateTime.of(
                        LocalDate.of(2021, 7, 9),
                        LocalTime.of(14, 0, 0, 0)
                ),
                "1234",
                "찜꽁",
                "찜꽁 2차 회의",
                space1
        );

        reservation3 = new Reservation(
                LocalDateTime.of(
                        LocalDate.of(2021, 7, 9),
                        LocalTime.of(18, 0, 0, 0)
                ),
                LocalDateTime.of(
                        LocalDate.of(2021, 7, 9),
                        LocalTime.of(23, 59, 59, 59)
                ),
                "6789",
                "찜꽁",
                "찜꽁 3차 회의",
                space1
        );

        reservation4 = new Reservation(
                LocalDateTime.of(
                        LocalDate.of(2021, 7, 10),
                        LocalTime.of(0, 0, 0, 0)
                ),
                LocalDateTime.of(
                        LocalDate.of(2021, 7, 10),
                        LocalTime.of(1, 0, 0, 0)
                ),
                "1234",
                "찜꽁",
                "찜꽁 4차 회의",
                space1
        );
    }

    @Test
    void findAllBySpaceId() {
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);
        reservationRepository.save(reservation4);

        List<Reservation> reservations = reservationRepository.findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween(
                space1.getId(),
                LocalDate.of(2021, 7, 9).atStartOfDay(),
                LocalDate.of(2021, 7, 10).atStartOfDay(),
                LocalDate.of(2021, 7, 9).atStartOfDay(),
                LocalDate.of(2021, 7, 10).atStartOfDay()
                );

        assertThat(reservations).hasSize(3);
    }
}
