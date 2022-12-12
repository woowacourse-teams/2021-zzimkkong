package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.ReservationType;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
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
    protected String email;
    protected String password;
    protected String name;
    protected String description;
    protected LoginUserEmail loginUserEmail;
    protected ReservationType reservationType;

    protected ReservationCreateDto(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest request,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.startDateTime = request.localStartDateTime();
        this.endDateTime = request.localEndDateTime();
        this.password = request.getPassword();
        this.email = request.getEmail();
        this.name = request.getName();
        this.description = request.getDescription();
        this.loginUserEmail = loginUserEmail;
        this.reservationType = ReservationType.of(apiType, this.loginUserEmail);

        // 모두 null
    }

    public static ReservationCreateDto of(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest reservationCreateUpdateWithPasswordRequest,
            final String apiType) {
        return new ReservationCreateDto(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                new LoginUserEmail(),
                apiType);
    }

    public static ReservationCreateDto of(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest reservationCreateUpdateWithPasswordRequest,
            final LoginUserEmail loginUserEmail,
            final String apiType) {
        return new ReservationCreateDto(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                loginUserEmail,
                apiType);
    }

    public boolean isManagerReservationForLoginUser() {
        return this.email != null;
    }

    public boolean isManagerReservationForNonLoginUser() {
        if (this instanceof ReservationUpdateDto) {
            return !isManagerReservationForLoginUser() && this.name != null;
        }
        return !isManagerReservationForLoginUser() && this.name != null && this.password != null;
    }
}
