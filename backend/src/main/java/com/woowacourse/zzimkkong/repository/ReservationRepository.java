package com.woowacourse.zzimkkong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woowacourse.zzimkkong.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
