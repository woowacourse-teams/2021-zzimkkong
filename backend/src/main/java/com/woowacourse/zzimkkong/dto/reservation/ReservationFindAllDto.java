package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Member;

import java.time.LocalDate;

public class ReservationFindAllDto {
    protected Long mapId;
    protected LocalDate date;
    protected Member manager;

    public ReservationFindAllDto() {
    }

    protected ReservationFindAllDto(
            final Long mapId,
            final LocalDate date,
            final Member manager) {
        this.mapId = mapId;
        this.date = date;
        this.manager = manager;
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date,
            final Member manager) {
        return new ReservationFindAllDto(mapId, date, manager);
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date) {
        return new ReservationFindAllDto(mapId, date, null);
    }

    public Long getMapId() {
        return mapId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Member getManager() {
        return manager;
    }
}
