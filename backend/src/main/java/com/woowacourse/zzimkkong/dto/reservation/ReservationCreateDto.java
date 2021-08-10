package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Member;

public class ReservationCreateDto {
    private Long mapId;
    private Long spaceId;
    private ReservationCreateUpdateWithPasswordRequest request;
    private Member manager;

    public ReservationCreateDto() {
    }

    private ReservationCreateDto(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateWithPasswordRequest request,
            final Member manager) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.request = request;
        this.manager = manager;
    }

    public static ReservationCreateDto of(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        return new ReservationCreateDto(mapId, spaceId, reservationCreateUpdateWithPasswordRequest, null);
    }

    public static ReservationCreateDto of(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            final Member manager) {
        return new ReservationCreateDto(mapId, spaceId, reservationCreateUpdateWithPasswordRequest, manager);
    }

    public Long getMapId() {
        return mapId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public ReservationCreateUpdateWithPasswordRequest getRequest() {
        return request;
    }

    public Member getManager() {
        return manager;
    }
}
