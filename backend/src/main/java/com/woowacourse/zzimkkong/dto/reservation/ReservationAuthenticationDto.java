package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationAuthenticationDto {
    private Long mapId;
    private Long spaceId;
    private Long reservationId;
    private String password;
    private Member manager;

    private ReservationAuthenticationDto(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest request,
            final Member manager) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.reservationId = reservationId;
        this.password = request.getPassword();
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
}
