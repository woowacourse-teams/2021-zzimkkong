package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
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
            final LoginEmailDto loginEmailDto) {
        super(mapId, spaceId, request, loginEmailDto);
        this.reservationId = reservationId;
    }

    public static ReservationUpdateDto of(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest request,
            final LoginEmailDto loginEmailDto) {
        return new ReservationUpdateDto(
                mapId,
                spaceId,
                reservationId,
                request,
                loginEmailDto);
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
                new LoginEmailDto());
    }
}
