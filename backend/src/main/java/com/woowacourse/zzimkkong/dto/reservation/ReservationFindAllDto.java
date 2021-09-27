package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReservationFindAllDto {
    protected Long mapId;
    protected LocalDate date;
    protected String loginEmail;

    protected ReservationFindAllDto(
            final Long mapId,
            final LocalDate date,
            final LoginEmailDto loginEmailDto) {
        this.mapId = mapId;
        this.date = date;
        this.loginEmail = loginEmailDto.getEmail();
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date,
            final LoginEmailDto loginEmailDto) {
        return new ReservationFindAllDto(mapId, date, loginEmailDto);
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date) {
        return new ReservationFindAllDto(mapId, date, new LoginEmailDto());
    }
}
