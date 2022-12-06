package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReservationFindDto extends ReservationFindAllDto {
    private Long spaceId;

    private ReservationFindDto(
            final Long mapId,
            final Long spaceId,
            final LocalDate date,
            final LoginUserEmail loginUserEmail) {
        super(mapId, date, loginUserEmail);
        this.spaceId = spaceId;
    }

    public static ReservationFindDto of(
            final Long mapId,
            final Long spaceId,
            final LocalDate date) {
        return new ReservationFindDto(mapId, spaceId, date, new LoginUserEmail());
    }

    public static ReservationFindDto of(
            final Long mapId,
            final Long spaceId,
            final LocalDate date,
            final LoginUserEmail loginUserEmail) {
        return new ReservationFindDto(mapId, spaceId, date, loginUserEmail);
    }
}
