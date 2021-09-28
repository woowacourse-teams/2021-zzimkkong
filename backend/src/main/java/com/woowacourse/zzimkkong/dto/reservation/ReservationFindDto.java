package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
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
            final LoginEmailDto loginEmailDto) {
        super(mapId, date, loginEmailDto);
        this.spaceId = spaceId;
    }

    public static ReservationFindDto of(
            final Long mapId,
            final Long spaceId,
            final LocalDate date) {
        return new ReservationFindDto(mapId, spaceId, date, new LoginEmailDto());
    }

    public static ReservationFindDto of(
            final Long mapId,
            final Long spaceId,
            final LocalDate date,
            final LoginEmailDto loginEmailDto) {
        return new ReservationFindDto(mapId, spaceId, date, loginEmailDto);
    }
}
