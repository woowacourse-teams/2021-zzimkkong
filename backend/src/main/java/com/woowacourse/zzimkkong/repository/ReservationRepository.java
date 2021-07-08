package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
