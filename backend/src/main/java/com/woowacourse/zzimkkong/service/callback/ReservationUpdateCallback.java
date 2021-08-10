package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;

public class ReservationUpdateCallback implements ReservationServiceCallback {
    private Reservation reservation;

    public ReservationUpdateCallback(final Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public void excludeTargetReservation(final Space space, final List<Reservation> reservationsOnDate) {
        if (reservation.getSpace().equals(space)) {
            reservationsOnDate.remove(reservation);
        }
    }
}
