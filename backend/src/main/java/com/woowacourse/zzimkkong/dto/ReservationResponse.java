package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.domain.Reservation;

import java.time.LocalDateTime;

public class ReservationResponse {
    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String name;
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
