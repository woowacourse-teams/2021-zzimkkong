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
    private String textPosition;

    @JsonProperty
    private String spaceColor;

    @JsonProperty
    private CoordinateResponse coordinate;

    @JsonProperty
    private List<ReservationResponse> reservations;

    public ReservationSpaceResponse() {
    }

    public ReservationSpaceResponse(Long spaceId, String spaceName, String spaceColor, String textPosition, CoordinateResponse coordinateResponse, List<ReservationResponse> reservations) {
        this.spaceId = spaceId;
        this.spaceName = spaceName;
        this.spaceColor = spaceColor;
        this.textPosition = textPosition;
        this.coordinate = coordinateResponse;
        this.reservations = reservations;
    }

    public static ReservationSpaceResponse of(final Map.Entry<Space, List<Reservation>> reservationsPerSpace) {
        List<ReservationResponse> reservations = reservationsPerSpace.getValue()
                .stream()
                .map(ReservationResponse::of)
                .collect(Collectors.toList());

        Space space = reservationsPerSpace.getKey();
        return new ReservationSpaceResponse(
                space.getId(),
                space.getName(),
                space.getColor(),
                space.getTextPosition(),
                CoordinateResponse.from(space.getCoordinate()),
                reservations
        );
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public String getSpaceColor() {
        return spaceColor;
    }
}
