package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Reservation;

public class ReservationSaveResponse {
    private Long id;

    public ReservationSaveResponse() {

    }

    private ReservationSaveResponse(Long id) {
        this.id = id;
    }

    public static ReservationSaveResponse of(Reservation reservation) {
        return new ReservationSaveResponse(reservation.getId());
    }

    public Long getId() {
        return id;
    }
}
