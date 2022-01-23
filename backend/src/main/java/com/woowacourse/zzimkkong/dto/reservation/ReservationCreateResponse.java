package com.woowacourse.zzimkkong.dto.reservation;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationCreateResponse {
    private Long id;
    private SlackResponse slackResponse;

    private ReservationCreateResponse(final Long id, final SlackResponse slackResponse) {
        this.id = id;
        this.slackResponse = slackResponse;
    }

    public static ReservationCreateResponse of(final Reservation reservation, final String sharingMapId, final String slackUrl) {
        SlackResponse slackResponse = SlackResponse.of(reservation, sharingMapId, slackUrl);
        return new ReservationCreateResponse(reservation.getId(), slackResponse);
    }
}
