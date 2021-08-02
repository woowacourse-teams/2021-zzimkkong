package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.Collections;
import java.util.Comparator;
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

    public static ReservationFindAllResponse of(final List<Space> spaces, final List<Reservation> reservations) {
        Map<Space, List<Reservation>> reservationGroups = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getSpace));
        spaces.stream()
                .filter(space -> !reservationGroups.containsKey(space))
                .forEach(space -> reservationGroups.put(space, Collections.emptyList()));

        for (final List<Reservation> each : reservationGroups.values()) {
            each.sort(Comparator.comparing(Reservation::getStartTime));
        }

        List<ReservationSpaceResponse> reservationSpaceResponses = reservationGroups.entrySet()
                .stream()
                .map(ReservationSpaceResponse::from)
                .sorted(Comparator.comparing(ReservationSpaceResponse::getSpaceId))
                .collect(Collectors.toList());

        return new ReservationFindAllResponse(reservationSpaceResponses);
    }
}
