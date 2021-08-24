package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;

public interface ExcludeReservationStrategy {
    void apply(final Space space, final List<Reservation> reservationsOnDate);
}
