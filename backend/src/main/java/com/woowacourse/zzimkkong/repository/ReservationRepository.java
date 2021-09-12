package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT COUNT(r) > 0 FROM Reservation r JOIN r.space s JOIN s.map m WHERE m.member = :member")
    boolean existsReservationsByMember(@Param("member") Member member);
}
