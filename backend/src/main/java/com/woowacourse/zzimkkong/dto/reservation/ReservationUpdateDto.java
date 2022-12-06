package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
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
            final LoginUserEmail loginUserEmail) {
        super(mapId, spaceId, request, loginUserEmail);
        this.reservationId = reservationId;
    }

    public static ReservationUpdateDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request,
            final LoginUserEmail loginUserEmail) {
        return new ReservationUpdateDto(
                mapId,
                spaceId,
                reservationId,
                request,
                loginUserEmail);
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
                new LoginUserEmail());
    }
}
