package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Boolean existsBySpaceIdAndStartTimeBetweenAndEndTimeBetween(Long spaceId, LocalDateTime firstStartDateTime, LocalDateTime firstEndTime, LocalDateTime secondStartDateTime, LocalDateTime secondEndDateTime);
}


