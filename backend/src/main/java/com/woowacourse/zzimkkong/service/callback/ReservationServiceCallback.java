package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;

public interface ReservationServiceCallback {
    void excludeTargetReservation(final Space space, final List<Reservation> reservationsOnDate);
}
