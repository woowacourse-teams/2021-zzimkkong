package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;

public class ExcludeReservationCreateStrategy implements ExcludeReservationStrategy {
    @Override
    public void apply(final Space space, final List<Reservation> reservationsOnDate) {
    }
}
