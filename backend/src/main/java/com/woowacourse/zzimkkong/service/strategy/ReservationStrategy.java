package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.UserType;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;

public interface ReservationStrategy {
    boolean supports(UserType userType);

    void validateManagerOfMap(final Map map, final LoginUserEmail loginUserEmail);

    void checkCorrectPassword(final Reservation reservation, final String password);

    boolean isManager();
}
