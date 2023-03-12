package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfiniteScrollResponse {
    private List<ReservationOwnerResponse> data;
    private Boolean hasNext;
    private Integer pageNumber;

    public static ReservationInfiniteScrollResponse of(final List<Reservation> reservations, final Boolean hasNext, final Integer pageNumber) {
        List<ReservationOwnerResponse> reservationOwnerResponses = reservations.stream()
                .map(ReservationOwnerResponse::from)
                .collect(Collectors.toList());

        return ReservationInfiniteScrollResponse.builder()
                .data(reservationOwnerResponses)
                .hasNext(hasNext)
                .pageNumber(pageNumber)
                .build();
    }
}
