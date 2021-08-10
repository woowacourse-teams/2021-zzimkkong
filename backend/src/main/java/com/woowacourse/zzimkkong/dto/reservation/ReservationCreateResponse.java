package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Reservation;
import lombok.Getter;

@Getter
public class ReservationCreateResponse {
    private Long id;

    public ReservationCreateResponse() {

    }

    private ReservationCreateResponse(final Long id) {
        this.id = id;
    }

    public static ReservationCreateResponse from(final Reservation reservation) {
        return new ReservationCreateResponse(reservation.getId());
    }
}
