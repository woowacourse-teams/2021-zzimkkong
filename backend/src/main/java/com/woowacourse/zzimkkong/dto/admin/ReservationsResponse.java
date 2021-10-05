package com.woowacourse.zzimkkong.dto.admin;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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

    public static ReservationsResponse of(List<ReservationResponse> reservations, PageInfo pageInfo) {
        return new ReservationsResponse(reservations, pageInfo);
    }

    public static ReservationsResponse from(Page<Reservation> allReservations) {
        List<ReservationResponse> responses = allReservations.map(ReservationResponse::fromAdmin).getContent();
        return of(responses, PageInfo.from(allReservations));
    }
}

