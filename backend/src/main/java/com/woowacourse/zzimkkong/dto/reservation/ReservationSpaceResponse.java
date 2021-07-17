package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservationSpaceResponse {
    @JsonProperty
    private Long spaceId;

    @JsonProperty
    private String spaceName;

    @JsonProperty
    private List<ReservationResponse> reservations;

    public ReservationSpaceResponse() {
    }

    private ReservationSpaceResponse(final Long spaceId, final String spaceName, final List<ReservationResponse> reservations) {
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.reservations = reservations;
    }

    public static ReservationSpaceResponse from(final Map.Entry<Space, List<Reservation>> reservationsPerSpace) {
        List<ReservationResponse> reservations = reservationsPerSpace.getValue()
                .stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());

        Space space = reservationsPerSpace.getKey();
        return new ReservationSpaceResponse(space.getId(), space.getName(), reservations);
    }

    public Long getSpaceId() {
        return spaceId;
    }
}
