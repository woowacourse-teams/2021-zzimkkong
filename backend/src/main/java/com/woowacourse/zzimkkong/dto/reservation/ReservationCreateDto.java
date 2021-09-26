package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.infrastructure.LoginEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationCreateDto {
    protected Long mapId;
    protected Long spaceId;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;
    protected String password;
    protected String name;
    protected String description;
    protected String loginEmail;

    protected ReservationCreateDto(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest request,
            final LoginEmail loginEmail) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.startDateTime = request.getStartDateTime();
        this.endDateTime = request.getEndDateTime();
        this.password = request.getPassword();
        this.name = request.getName();
        this.description = request.getDescription();
        this.loginEmail = loginEmail.getEmail();
    }

    public static ReservationCreateDto of(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest reservationCreateUpdateWithPasswordRequest) {
        return new ReservationCreateDto(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                new LoginEmail());
    }

    public static ReservationCreateDto of(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest reservationCreateUpdateWithPasswordRequest,
            final LoginEmail loginEmail) {
        return new ReservationCreateDto(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                loginEmail);
    }
}
