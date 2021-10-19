package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.repository.MemberRepository;

public interface ReservationStrategy {
    // todo members 불필요
    void validateManagerOfMap(final Map map, final MemberRepository members, final String loginEmail);

    void checkCorrectPassword(final Reservation reservation, final String password);
}
