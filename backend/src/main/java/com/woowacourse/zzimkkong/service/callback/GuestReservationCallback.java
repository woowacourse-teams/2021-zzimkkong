package com.woowacourse.zzimkkong.service.callback;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.reservation.ReservationPasswordException;

public class GuestReservationCallback implements ReservationControllerCallback {
    @Override
    public void validateManagerOfMap(final Map map, final Member manager) {
    }

    @Override
    public void checkCorrectPassword(final Reservation reservation, final String password) {
        if (reservation.isWrongPassword(password)) {
            throw new ReservationPasswordException();
        }
    }

    @Override
    public SlackResponse createSlackResponse(final Reservation reservation) {
        return null;
    }
}
