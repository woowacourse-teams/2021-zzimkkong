package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Reservation;

import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.dto.Validator.DATEFORMAT;

public class ReservationResponse {
    @JsonProperty
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATEFORMAT)
    private LocalDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATEFORMAT)
    private LocalDateTime endDateTime;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;

    public ReservationResponse() {
    }

    private ReservationResponse(
            final Long id,
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final String name,
            final String description) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.name = name;
        this.description = description;
    }

    public static ReservationResponse of(final Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getUserName(),
                reservation.getDescription()
        );
    }
}
