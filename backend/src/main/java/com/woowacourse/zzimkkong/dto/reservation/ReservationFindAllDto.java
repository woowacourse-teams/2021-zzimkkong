package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReservationFindAllDto {
    protected Long mapId;
    protected LocalDate date;
    protected LoginUserEmail loginUserEmail;

    protected ReservationFindAllDto(
            final Long mapId,
            final LocalDate date,
            final LoginUserEmail loginUserEmail) {
        this.mapId = mapId;
        this.date = date;
        this.loginUserEmail = loginUserEmail;
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date,
            final LoginUserEmail loginUserEmail) {
        return new ReservationFindAllDto(mapId, date, loginUserEmail);
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date) {
        return new ReservationFindAllDto(mapId, date, new LoginUserEmail());
    }
}
