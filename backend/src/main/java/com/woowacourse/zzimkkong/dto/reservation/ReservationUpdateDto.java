package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Member;

public class ReservationUpdateDto extends ReservationCreateDto {
    private Long reservationId;

    public ReservationUpdateDto() {
    }

    private ReservationUpdateDto(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request,
            final Member manager) {
        super(mapId, spaceId, request, manager);
        this.reservationId = reservationId;
    }

    public static ReservationUpdateDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request,
            final Member manager) {
        return new ReservationUpdateDto(
                mapId,
                spaceId,
                reservationId,
                request,
                manager);
    }

    public static ReservationUpdateDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request) {
        return new ReservationUpdateDto(
                mapId,
                spaceId,
                reservationId,
                request,
                new Member());
    }

    public Long getReservationId() {
        return reservationId;
    }
}
