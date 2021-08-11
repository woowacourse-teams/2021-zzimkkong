package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReservationFindAllDto {
    protected Long mapId;
    protected LocalDate date;
    protected Member manager;

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
        return new ReservationFindAllDto(mapId, date, new Member());
    }
}
