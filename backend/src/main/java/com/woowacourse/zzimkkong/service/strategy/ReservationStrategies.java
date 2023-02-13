package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.ReservationType;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationStrategies {
    private final List<ReservationStrategy> reservationStrategies;

    public ReservationStrategies(final List<ReservationStrategy> reservationStrategies) {
        this.reservationStrategies = reservationStrategies;
    }

    public ReservationStrategy getStrategyByReservationType(final ReservationType reservationType) {
        return reservationStrategies.stream()
                .filter(strategy -> strategy.supports(reservationType))
                .findFirst()
                .orElseThrow(ZzimkkongException::new);
    }
}
