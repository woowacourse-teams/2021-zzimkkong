package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
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
            final LoginEmail loginEmail) {
        this.mapId = mapId;
        this.date = date;
        this.loginEmail = loginEmail.getEmail();
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date,
            final LoginEmail loginEmail) {
        return new ReservationFindAllDto(mapId, date, loginEmail);
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date) {
        return new ReservationFindAllDto(mapId, date, new LoginEmail());
    }
}
