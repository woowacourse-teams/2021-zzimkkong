package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween(
            final Long spaceId,
            final LocalDateTime firstStartTime,
            final LocalDateTime firstEndTime,
            final LocalDateTime secondStartTime,
            final LocalDateTime secondEndTime);
}
