package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Member;

import java.time.LocalDate;

public class ReservationFindDto extends ReservationFindAllDto {
    private Long spaceId;

    public ReservationFindDto() {
    }

    private ReservationFindDto(final Long mapId, final Long spaceId, final LocalDate date, final Member manager) {
        super(mapId, date, manager);
        this.spaceId = spaceId;
    }

    public static ReservationFindDto of(final Long mapId, final Long spaceId, final LocalDate date) {
        return new ReservationFindDto(mapId, spaceId, date, null);
    }

    public static ReservationFindDto of(final Long mapId, final Long spaceId, final LocalDate date, final Member manager) {
        return new ReservationFindDto(mapId, spaceId, date, manager);
    }

    public Long getSpaceId() {
        return spaceId;
    }
}
