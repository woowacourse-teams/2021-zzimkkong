package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Reservation;

public class ReservationCreateResponse {
    private Long id;

    public ReservationCreateResponse() {

    }

    private ReservationCreateResponse(Long id) {
        this.id = id;
    }

    public static ReservationCreateResponse from(Reservation reservation) {
        return new ReservationCreateResponse(reservation.getId());
    }

    public Long getId() {
        return id;
    }
}
