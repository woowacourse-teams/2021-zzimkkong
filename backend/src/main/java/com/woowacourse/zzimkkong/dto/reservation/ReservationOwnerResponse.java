package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATETIME_FORMAT;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
@Builder
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
    private String mapName;
    private Long spaceId;
    private String spaceName;
    private String spaceColor;

    public static ReservationOwnerResponse from(final Reservation reservation) {
        Member member = reservation.getMember();
        Space space = reservation.getSpace();
        Map map = space.getMap();

        return ReservationOwnerResponse.builder()
                .id(reservation.getId())
                .startDateTime(reservation.getStartTime().atZone(UTC.toZoneId()))
                .endDateTime(reservation.getEndTime().atZone(UTC.toZoneId()))
                .memberId(member.getId())
                .name(member.getUserName())
                .description(reservation.getDescription())
                .mapId(map.getId())
                .mapName(map.getName())
                .spaceId(space.getId())
                .spaceName(space.getName())
                .spaceColor(space.getColor())
                .build();
    }
}
