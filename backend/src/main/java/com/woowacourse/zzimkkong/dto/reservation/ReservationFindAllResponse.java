package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        for (final List<Reservation> each : reservationGroups.values()) {
            each.sort(Comparator.comparing(Reservation::getStartTime));
        }

        List<ReservationSpaceResponse> reservationSpaceResponses = reservationGroups.entrySet()
                .stream()
                .map(ReservationSpaceResponse::of)
                .sorted(Comparator.comparing(ReservationSpaceResponse::getSpaceId))
                .collect(Collectors.toList());

        return new ReservationFindAllResponse(reservationSpaceResponses);
    }
}
