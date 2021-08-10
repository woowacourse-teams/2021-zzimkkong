package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class GuestReservationCallback implements ReservationCallback {
    @Override
    public void validateManagerOfMap(final Map map, final Member manager) {

    }
}
