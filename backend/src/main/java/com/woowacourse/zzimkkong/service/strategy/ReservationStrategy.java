package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Reservation;

public interface ReservationStrategy {
    void validateManagerOfMap(final Map map, final String loginEmail);

    void checkCorrectPassword(final Reservation reservation, final String password);

    boolean isManager();
}
