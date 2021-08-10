package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Reservation;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReservationFindResponse {
    @JsonProperty
    private List<ReservationResponse> reservations;

    public ReservationFindResponse() {
    }

    private ReservationFindResponse(final List<ReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservationFindResponse from(final List<Reservation> reservations) {
        reservations.sort(Comparator.comparing(Reservation::getStartTime));

        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());

        return new ReservationFindResponse(reservationResponses);
    }
}
