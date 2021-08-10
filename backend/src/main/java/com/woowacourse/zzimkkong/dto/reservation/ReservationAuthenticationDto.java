package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Member;

public class ReservationAuthenticationDto {
    private Long mapId;
    private Long spaceId;
    private Long reservationId;
    private ReservationPasswordAuthenticationRequest request;
    private Member manager;

    public ReservationAuthenticationDto() {
    }

    private ReservationAuthenticationDto(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest request,
            final Member manager) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.reservationId = reservationId;
        this.request = request;
        this.manager = manager;
    }

    public static ReservationAuthenticationDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest request) {
        return new ReservationAuthenticationDto(
                mapId,
                spaceId,
                reservationId,
                request,
                new Member());
    }

    public static ReservationAuthenticationDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final Member manager) {
        return new ReservationAuthenticationDto(
                mapId,
                spaceId,
                reservationId,
                new ReservationPasswordAuthenticationRequest(),
                manager);
    }

    public Long getMapId() {
        return mapId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public ReservationPasswordAuthenticationRequest getRequest() {
        return request;
    }

    public Member getManager() {
        return manager;
    }

    public String getPassword() {
        return request.getPassword();
    }
}
