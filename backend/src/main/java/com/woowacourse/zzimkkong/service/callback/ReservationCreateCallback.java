package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;

public class ReservationCreateCallback implements ReservationServiceCallback {
    @Override
    public void excludeTargetReservation(final Space space, final List<Reservation> reservationsOnDate) {
    }
}
