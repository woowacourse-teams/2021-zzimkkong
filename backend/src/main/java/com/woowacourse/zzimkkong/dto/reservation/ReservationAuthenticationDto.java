package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationAuthenticationDto {
    private Long mapId;
    private Long spaceId;
    private Long reservationId;
    private String password;
    private String loginEmail;

    private ReservationAuthenticationDto(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest request,
            final LoginEmail loginEmail) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.reservationId = reservationId;
        this.password = request.getPassword();
        this.loginEmail = loginEmail.getEmail();
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
                new LoginEmail());
    }

    public static ReservationAuthenticationDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final LoginEmail loginEmail) {
        return new ReservationAuthenticationDto(
                mapId,
                spaceId,
                reservationId,
                new ReservationPasswordAuthenticationRequest(),
                loginEmail);
    }
}
