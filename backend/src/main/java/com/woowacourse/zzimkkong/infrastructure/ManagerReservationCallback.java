package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import org.springframework.stereotype.Component;

@Component
public class ManagerReservationCallback implements ReservationCallback {
    @Override
    public void validateManagerOfMap(final Map map, final Member manager) {
        if (map.isNotOwnedBy(manager)) {
            throw new NoAuthorityOnMapException();
        }
    }
}
