package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;

public interface ReservationCallback {
    void validateManagerOfMap(final Map map, final Member manager);
}
