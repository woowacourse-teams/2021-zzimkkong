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
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        super(mapId, spaceId, request, loginUserEmail, apiType);
        this.reservationId = reservationId;
    }

    public static ReservationUpdateDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        return new ReservationUpdateDto(
                mapId,
                spaceId,
                reservationId,
                request,
                loginUserEmail,
                apiType);
    }

    public static ReservationUpdateDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request,
            final String apiType) {
        return new ReservationUpdateDto(
                mapId,
                spaceId,
                reservationId,
                request,
                new LoginUserEmail(),
                apiType);
    }
}
