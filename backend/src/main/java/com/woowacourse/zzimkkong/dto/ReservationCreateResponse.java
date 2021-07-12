package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.domain.Reservation;

public class ReservationCreateResponse {
    private Long id;

    public ReservationCreateResponse() {

    }

    private ReservationCreateResponse(Long id) {
        this.id = id;
    }

    public static ReservationCreateResponse of(Reservation reservation) {
        return new ReservationCreateResponse(reservation.getId());
    }

    public Long getId() {
        return id;
    }
}
