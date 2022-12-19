package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@LogMethodExecutionTime(group = "repository")
public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
    @Query(value = "SELECT r FROM Reservation r " +
            "WHERE r.space.id IN :spaceIds AND " +
            "r.reservationTime.date >= :lowerBoundDate AND " +
            "r.reservationTime.date <= :upperBoundDate")
    List<Reservation> findAllBySpaceIdInAndDateBetween(
            @Param("spaceIds") final Collection<Long> spaceIds,
            @Param("lowerBoundDate") final LocalDate lowerBoundDate,
            @Param("upperBoundDate") final LocalDate upperBoundDate);

    List<Reservation> findAllBySpaceIdInAndReservationTimeDate(
            final Collection<Long> spaceIds,
            final LocalDate date);

    Boolean existsBySpaceIdAndReservationTimeEndTimeAfter(final Long spaceId, final LocalDateTime now);

    @Query(value = "select r from Reservation r inner join fetch r.space s " +
            "inner join fetch s.map m " +
            "inner join fetch m.member " +
            "order by r.id",
            countQuery = "select count(r) from Reservation r")
    Page<Reservation> findAllByFetch(Pageable pageable);
}
