package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
    List<Reservation> findAllBySpaceIdInAndDate(final Collection<Long> spaceIds, final LocalDate date);

    Boolean existsBySpaceIdAndEndTimeAfter(Long spaceId, LocalDateTime now);
}
