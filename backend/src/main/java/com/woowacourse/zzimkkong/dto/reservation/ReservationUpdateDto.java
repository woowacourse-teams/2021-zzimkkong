package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationUpdateDto extends ReservationCreateDto {
    private Long reservationId;

    private ReservationUpdateDto(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request,
            final LoginEmail loginEmail) {
        super(mapId, spaceId, request, loginEmail);
        this.reservationId = reservationId;
    }

    public static ReservationUpdateDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request,
            final LoginEmail loginEmail) {
        return new ReservationUpdateDto(
                mapId,
                spaceId,
                reservationId,
                request,
                loginEmail);
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
                new LoginEmail());
    }
}
