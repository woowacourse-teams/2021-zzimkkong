package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;

public class ExcludeReservationUpdateStrategy implements ExcludeReservationStrategy {
    private final Reservation reservation;

    public ExcludeReservationUpdateStrategy(final Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public void apply(final Space space, final List<Reservation> reservationsOnDate) {
        if (reservation.getSpace().equals(space)) {
            reservationsOnDate.remove(reservation);
        }
    }
}
