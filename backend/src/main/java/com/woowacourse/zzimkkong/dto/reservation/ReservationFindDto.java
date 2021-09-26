package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.infrastructure.LoginEmail;
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
            final LoginEmail loginEmail) {
        super(mapId, date, loginEmail);
        this.spaceId = spaceId;
    }

    public static ReservationFindDto of(
            final Long mapId,
            final Long spaceId,
            final LocalDate date) {
        return new ReservationFindDto(mapId, spaceId, date, new LoginEmail());
    }

    public static ReservationFindDto of(
            final Long mapId,
            final Long spaceId,
            final LocalDate date,
            final LoginEmail loginEmail) {
        return new ReservationFindDto(mapId, spaceId, date, loginEmail);
    }
}
