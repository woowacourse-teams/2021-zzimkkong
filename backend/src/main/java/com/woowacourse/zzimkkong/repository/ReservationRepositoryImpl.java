package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final EntityManager entityManager;

    public ReservationRepositoryImpl(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean existsByMemberAndEndTimeAfter(final Member member, final LocalDateTime now) {
        return entityManager.createQuery(
                        "SELECT COUNT(r) > 0 FROM Reservation r " +
                                "JOIN r.space s JOIN s.map m " +
                                "WHERE m.member = :member AND r.reservationTime.endTime >= :currentTime", Boolean.class)
                .setParameter("member", member)
                .setParameter("currentTime", now)
                .getSingleResult();
    }
}
