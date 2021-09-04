package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;

public class ExcludeReservationCreateStrategy implements ExcludeReservationStrategy {
    @Override
    public void apply(final Space space, final List<Reservation> reservationsOnDate) {
        // 예약 생성 시는 검증 전 예약을 제외하지 않아도 되므로 생략
    }
}
