package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import org.springframework.stereotype.Component;

@Component
public class ManagerReservationStrategy implements ReservationStrategy {
    @Override
    public boolean supports(final UserType userType) {
        return UserType.MANAGER.equals(userType);
    }

    @Override
    public void validateManagerOfMap(final Map map, final LoginUserEmail loginUserEmail) {
        Member manager = map.getMember();
        if (!manager.hasEmail(loginUserEmail.getEmail())) {
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
