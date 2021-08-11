package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationCreateDto {
    protected Long mapId;
    protected Long spaceId;
    protected ReservationCreateUpdateRequest request;
    protected Member manager;

    protected ReservationCreateDto(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest request,
            final Member manager) {
        this.mapId = mapId;
        this.spaceId = spaceId;
        this.request = request;
        this.manager = manager;
    }

    public static ReservationCreateDto of(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest reservationCreateUpdateWithPasswordRequest) {
        return new ReservationCreateDto(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                new Member());
    }

    public static ReservationCreateDto of(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateRequest reservationCreateUpdateWithPasswordRequest,
            final Member manager) {
        return new ReservationCreateDto(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                manager);
    }

    public LocalDateTime getStartDateTime() {
        return request.getStartDateTime();
    }

    public LocalDateTime getEndDateTime() {
        return request.getEndDateTime();
    }

    public String getPassword() {
        return request.getPassword();
    }

    public String getName() {
        return request.getName();
    }

    public String getDescription() {
        return request.getDescription();
    }
}
