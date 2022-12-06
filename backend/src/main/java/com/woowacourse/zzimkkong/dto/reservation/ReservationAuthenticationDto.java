package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
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
            final LoginUserEmail loginUserEmail) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.reservationId = reservationId;
        this.password = request.getPassword();
        this.loginEmail = loginUserEmail.getEmail();
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
                new LoginUserEmail());
    }

    public static ReservationAuthenticationDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final LoginUserEmail loginUserEmail) {
        return new ReservationAuthenticationDto(
                mapId,
                spaceId,
                reservationId,
                new ReservationPasswordAuthenticationRequest(),
                loginUserEmail);
    }
}
