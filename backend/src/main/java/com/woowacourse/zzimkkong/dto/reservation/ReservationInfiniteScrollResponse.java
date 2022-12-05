package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReservationInfiniteScrollResponse {
    private List<Reservation> data;
    private Boolean hasNext;
    private Integer pageNumber;

    public static ReservationInfiniteScrollResponse of(final List<Reservation> data, final Boolean hasNext, final Integer pageNumber) {
        return ReservationInfiniteScrollResponse.builder()
                .data(data)
                .hasNext(hasNext)
                .pageNumber(pageNumber)
                .build();
    }
}
