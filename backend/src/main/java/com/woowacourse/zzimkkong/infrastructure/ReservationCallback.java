package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;

public interface ReservationCallback {
    void validateManagerOfMap(final Map map, final Member manager);
}
