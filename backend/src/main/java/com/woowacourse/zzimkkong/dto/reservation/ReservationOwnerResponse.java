package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATETIME_FORMAT;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationOwnerResponse {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    private ZonedDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    private ZonedDateTime endDateTime;
    private Long memberId;
    private String name;
    private String description;
    private Long mapId;
    private String sharingMapId;
    private String mapName;
    private Long spaceId;
    private String spaceName;
    private String spaceColor;

    public static ReservationOwnerResponse from(final Reservation reservation) {
        Space space = reservation.getSpace();
        Map map = space.getMap();

        return ReservationOwnerResponse.builder()
                .id(reservation.getId())
                .startDateTime(reservation.getStartTime().atZone(UTC.toZoneId()))
                .endDateTime(reservation.getEndTime().atZone(UTC.toZoneId()))
                .memberId(reservation.getMemberId())
                .name(reservation.getUserName())
                .description(reservation.getDescription())
                .mapId(map.getId())
                .sharingMapId(map.getSharingMapId())
                .mapName(map.getName())
                .spaceId(space.getId())
                .spaceName(space.getName())
                .spaceColor(space.getColor())
                .build();
    }
}
