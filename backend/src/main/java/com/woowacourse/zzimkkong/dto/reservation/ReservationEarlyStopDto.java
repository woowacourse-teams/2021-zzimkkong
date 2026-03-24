package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.ReservationType;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationEarlyStopDto {

    private Long mapId;
    private Long spaceId;
    private Long reservationId;
    private String email;
    private String password;
    private LoginUserEmail loginUserEmail;
    private ReservationType reservationType;

    private ReservationEarlyStopDto(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationEarlyStopRequest request,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        this.reservationId = reservationId;
        this.spaceId = spaceId;
        this.mapId = mapId;
        this.password = request.getPassword();
        this.email = request.getEmail();
        this.loginUserEmail = loginUserEmail;
        this.reservationType = ReservationType.of(apiType, getReservationEmail(apiType));
    }

    public static ReservationEarlyStopDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationEarlyStopRequest request,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        return new ReservationEarlyStopDto(
                mapId,
                spaceId,
                reservationId,
                request,
                loginUserEmail,
                apiType);
    }

    public static ReservationEarlyStopDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationEarlyStopRequest request,
            final String apiType) {
        return new ReservationEarlyStopDto(
                mapId,
                spaceId,
                reservationId,
                request,
                new LoginUserEmail(),
                apiType);
    }

    private LoginUserEmail getReservationEmail(String apiType) {
        if (ReservationType.Constants.GUEST.equals(apiType)) {
            return this.loginUserEmail;
        }
        return LoginUserEmail.from(this.email);
    }
}
