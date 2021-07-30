package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.service.ReservationService;

public abstract class ReservationController<Service extends ReservationService> {
    protected final Service reservationService;

    protected ReservationController(final Service reservationService) {
        this.reservationService = reservationService;
    }
}
