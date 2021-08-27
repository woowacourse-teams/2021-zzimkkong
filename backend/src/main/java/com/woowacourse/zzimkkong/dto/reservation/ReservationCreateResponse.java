package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationCreateResponse {
    private Long id;

    private ReservationCreateResponse(final Long id) {
        this.id = id;
    }

    public static ReservationCreateResponse from(final Reservation reservation) {
        return new ReservationCreateResponse(reservation.getId());
    }
}
