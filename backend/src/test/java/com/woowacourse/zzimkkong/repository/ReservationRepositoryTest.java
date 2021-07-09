package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationRepositoryTest extends RepositoryTest {
    @Autowired
    private ReservationRepository reservations;

    private static final LocalDateTime startTime = LocalDateTime.of(2021, 5, 6, 16, 23, 0);
    private static final LocalDateTime endTime = LocalDateTime.of(2021, 5, 6, 19, 23, 0);
    public static final Space SPACE = new Space("회의실", MapRepositoryTest.MAP);
    public static final Reservation RESERVATION = new Reservation(startTime, endTime,"zzimkkong", "bada","1234", SPACE);

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
