package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.UserType;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationStrategies {
    private final List<ReservationStrategy> reservationStrategies;

    public ReservationStrategies(final List<ReservationStrategy> reservationStrategies) {
        this.reservationStrategies = reservationStrategies;
    }

    public ReservationStrategy getStrategyByUserType(final UserType userType) {
        return reservationStrategies.stream()
                .filter(strategy -> strategy.supports(userType))
                .findFirst()
                .orElseThrow(ZzimkkongException::new);
    }
}
