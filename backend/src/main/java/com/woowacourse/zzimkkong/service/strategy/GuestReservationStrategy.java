package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.exception.reservation.ReservationPasswordException;
import org.springframework.stereotype.Component;

@Component
public class GuestReservationStrategy implements ReservationStrategy {
    @Override
    public boolean supports(final UserType userType) {
        return UserType.GUEST.equals(userType);
    }

    @Override
    public void validateManagerOfMap(final Map map, final LoginUserEmail loginUserEmail) {
        // guest는 맵의 관리자 확인과정 생략
    }

    @Override
    public void checkCorrectPassword(final Reservation reservation, final String password) {
        if (reservation.isWrongPassword(password)) {
            throw new ReservationPasswordException();
        }
    }

    @Override
    public boolean isManager() {
        return false;
    }
}
