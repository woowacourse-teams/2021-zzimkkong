package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
            final Collection<Long> spaceIds,
            final LocalDateTime firstStartTime,
            final LocalDateTime firstEndTime,
            final LocalDateTime secondStartTime,
            final LocalDateTime secondEndTime);

    Boolean existsBySpaceIdAndEndTimeAfter(Long spaceId, LocalDateTime now);
}
