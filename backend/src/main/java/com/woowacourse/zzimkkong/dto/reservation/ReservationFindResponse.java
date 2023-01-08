package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReservationFindResponse {
    @JsonProperty
    private List<ReservationResponse> reservations;

    private ReservationFindResponse(final List<ReservationResponse> reservations) {
        this.reservations = reservations;
    }

    public static ReservationFindResponse from(final List<Reservation> reservations, final Member loginUser) {
        reservations.sort(Comparator.comparing(Reservation::getStartTime));

        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(reservation -> ReservationResponse.from(reservation, loginUser))
                .collect(Collectors.toList());

        return new ReservationFindResponse(reservationResponses);
    }
}
