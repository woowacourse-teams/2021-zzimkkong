package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Boolean existsBySpaceIdAndStartTimeBetween(Long spaceId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    Boolean existsBySpaceIdAndEndTimeBetween(Long spaceId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
