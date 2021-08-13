package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;

public class ManagerReservationStrategy implements ReservationStrategy {
    @Override
    public void validateManagerOfMap(final Map map, final Member manager) {
        if (map.isNotOwnedBy(manager)) {
            throw new NoAuthorityOnMapException();
        }
    }

    @Override
    public void checkCorrectPassword(final Reservation reservation, final String password) {
    }

    @Override
    public SlackResponse createSlackResponse(final Reservation reservation) {
        return SlackResponse.from(reservation);
    }
}
