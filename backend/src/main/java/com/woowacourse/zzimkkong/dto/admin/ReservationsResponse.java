package com.woowacourse.zzimkkong.dto.admin;

import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationsResponse {
    private List<ReservationResponse> reservations;
    private PageInfo pageInfo;

    private ReservationsResponse(List<ReservationResponse> reservations, PageInfo pageInfo) {
        this.reservations = reservations;
        this.pageInfo = pageInfo;
    }

    public static ReservationsResponse from(List<ReservationResponse> reservations, PageInfo pageInfo) {
        return new ReservationsResponse(reservations, pageInfo);
    }
}

