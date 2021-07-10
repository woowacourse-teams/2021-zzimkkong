package com.woowacourse.zzimkkong.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.woowacourse.zzimkkong.domain.Reservation;

import java.time.LocalDateTime;

public class ReservationResponse {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(final LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(final LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
