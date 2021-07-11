package com.woowacourse.zzimkkong.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationFindAllResponse {
    @JsonProperty
    private List<ReservationSpaceResponse> data;

    public ReservationFindAllResponse() {
    }

    private ReservationFindAllResponse(final List<ReservationSpaceResponse> data) {
        this.data = data;
    }

    public static ReservationFindAllResponse of(List<Reservation> reservations) {
        Map<Space, List<Reservation>> reservationGroups = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getSpace));

        List<ReservationSpaceResponse> reservationSpaceResponses = reservationGroups.entrySet()
                .stream()
                .map(ReservationSpaceResponse::of)
                .collect(Collectors.toList());

        return new ReservationFindAllResponse(reservationSpaceResponses);
    }
}
