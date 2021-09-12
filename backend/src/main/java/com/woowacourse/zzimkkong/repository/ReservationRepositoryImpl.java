package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final EntityManager entityManager;

    public ReservationRepositoryImpl(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean existsReservationsByMemberFromToday(Member member) {
        return entityManager.createQuery(
                        "SELECT COUNT(r) > 0 FROM Reservation r " +
                                "JOIN r.space s JOIN s.map m " +
                                "WHERE m.member = :member AND r.endTime >= :currentTime", Boolean.class)
                .setParameter("member", member)
                .setParameter("currentTime", LocalDateTime.now())
                .getSingleResult();
    }
}
