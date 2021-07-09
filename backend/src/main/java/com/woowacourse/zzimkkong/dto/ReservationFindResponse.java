package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.domain.Reservation;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationFindResponse {
    private List<ReservationResponse> reservations;

    private ReservationFindResponse(final List<ReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservationFindResponse of(final List<Reservation> reservations) {
        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(ReservationResponse::of)
                .collect(Collectors.toList());
        return new ReservationFindResponse(reservationResponses);
    }
}
