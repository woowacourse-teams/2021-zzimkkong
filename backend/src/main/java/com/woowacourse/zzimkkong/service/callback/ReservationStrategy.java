package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;

public interface ReservationStrategy {
    void validateManagerOfMap(final Map map, final Member manager);

    void checkCorrectPassword(final Reservation reservation, final String password);

    SlackResponse createSlackResponse(final Reservation reservation);
}
