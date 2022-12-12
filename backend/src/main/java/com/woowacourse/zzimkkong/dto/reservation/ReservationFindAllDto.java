package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.ReservationType;
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
    protected ReservationType reservationType;

    protected ReservationFindAllDto(
            final Long mapId,
            final LocalDate date,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        this.mapId = mapId;
        this.date = date;
        this.loginUserEmail = loginUserEmail;
        this.reservationType = ReservationType.of(apiType, loginUserEmail);
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        return new ReservationFindAllDto(mapId, date, loginUserEmail, apiType);
    }

    public static ReservationFindAllDto of(
            final Long mapId,
            final LocalDate date,
            final String apiType) {
        return new ReservationFindAllDto(mapId, date, new LoginUserEmail(), apiType);
    }
}
