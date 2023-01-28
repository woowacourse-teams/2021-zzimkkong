package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATETIME_FORMAT;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationResponse {
    @JsonProperty
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    private ZonedDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_FORMAT)
    private ZonedDateTime endDateTime;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private Boolean isLoginReservation;
    @JsonProperty
    private Boolean isMyReservation;

    private Long spaceId;
    private Long mapId;
    private Long managerId;

    private ReservationResponse(
            final Long id,
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final String name,
            final String description,
            final Boolean isLoginReservation,
            final Boolean isMyReservation) {
        this.id = id;
        this.startDateTime = startDateTime.atZone(UTC.toZoneId());
        this.endDateTime = endDateTime.atZone(UTC.toZoneId());
        this.name = name;
        this.description = description;
        this.isLoginReservation = isLoginReservation;
        this.isMyReservation = isMyReservation;
    }

    public ReservationResponse(
            final Long id,
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final String name,
            final String description,
            final Boolean isLoginReservation,
            final Boolean isMyReservation,
            final Long spaceId,
            final Long mapId,
            final Long managerId) {
        this(id, startDateTime, endDateTime, name, description, isLoginReservation, isMyReservation);
        this.spaceId = spaceId;
        this.mapId = mapId;
        this.managerId = managerId;
    }

    public static ReservationResponse from(final Reservation reservation, final Member loginUser) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getUserName(),
                reservation.getDescription(),
                reservation.hasMember(),
                reservation.isOwnedBy(loginUser)
        );
    }

    public static ReservationResponse fromAdmin(final Reservation reservation) {
        Space space = reservation.getSpace();
        Map map = space.getMap();
        Member member = map.getMember();

        return new ReservationResponse(
                reservation.getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getUserName(),
                reservation.getDescription(),
                reservation.hasMember(),
                false,
                space.getId(),
                map.getId(),
                member.getId()
        );
    }
}
