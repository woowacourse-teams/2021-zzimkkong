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
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        super(mapId, date, loginUserEmail, apiType);
        this.spaceId = spaceId;
    }

    public static ReservationFindDto of(
            final Long mapId,
            final Long spaceId,
            final LocalDate date,
            final String apiType) {
        return new ReservationFindDto(mapId, spaceId, date, new LoginUserEmail(), apiType);
    }

    public static ReservationFindDto of(
            final Long mapId,
            final Long spaceId,
            final LocalDate date,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        return new ReservationFindDto(mapId, spaceId, date, loginUserEmail, apiType);
    }
}
