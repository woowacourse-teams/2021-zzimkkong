package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;

public class ManagerReservationStrategy implements ReservationStrategy {
    @Override
    public void validateManagerOfMap(final Map map, final String loginEmail) {
        Member manager = map.getMember();
        if (!manager.isSameEmail(loginEmail)) {
            throw new NoAuthorityOnMapException();
        }
    }

    @Override
    public void checkCorrectPassword(final Reservation reservation, final String password) {
        // manager는 비밀번호 확인과정이 없으므로 생략
    }

    @Override
    public boolean isManager() {
        return true;
    }
}
