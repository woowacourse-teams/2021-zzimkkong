package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.ReservationType;
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
    private LoginUserEmail loginUserEmail = new LoginUserEmail();
    private ReservationType reservationType;

    private ReservationAuthenticationDto(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest request,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.reservationId = reservationId;
        this.password = request.getPassword();
        this.loginUserEmail = loginUserEmail;
        this.reservationType = ReservationType.of(apiType, loginUserEmail);
    }

    public static ReservationAuthenticationDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest request,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        return new ReservationAuthenticationDto(
                mapId,
                spaceId,
                reservationId,
                request,
                loginUserEmail,
                apiType);
    }

    public static ReservationAuthenticationDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest request,
            final String apiType) {
        return new ReservationAuthenticationDto(
                mapId,
                spaceId,
                reservationId,
                request,
                new LoginUserEmail(),
                apiType);
    }

    public static ReservationAuthenticationDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        return new ReservationAuthenticationDto(
                mapId,
                spaceId,
                reservationId,
                new ReservationPasswordAuthenticationRequest(),
                loginUserEmail,
                apiType);
    }
}
