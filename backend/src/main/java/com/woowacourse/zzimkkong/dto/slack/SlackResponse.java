package com.woowacourse.zzimkkong.dto.slack;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.KST;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
@NoArgsConstructor
public class SlackResponse {
    private String mapName;
    private String spaceName;
    private String userName;
    private String reservationTime;
    private String description;
    private String sharingMapId;
    private String slackUrl;

    private SlackResponse(
            final String mapName,
            final String spaceName,
            final String userName,
            final LocalDateTime startTime,
            final LocalDateTime endTime,
            final String description,
            final String sharingMapId,
            final String slackUrl) {
        this.mapName = "교육장명 : " + mapName;
        this.spaceName = "회의실명 : " + spaceName;
        this.userName = "예약자명 : " + userName;
        this.reservationTime = "예약시간 : " + startTime + " ~ " + endTime;
        this.description = "예약내용 : " + description;
        this.sharingMapId = sharingMapId;
        this.slackUrl = slackUrl;
    }

    public static SlackResponse of(final Reservation reservation, final String sharingMapId, final String slackUrl) {
        LocalDateTime reservationStartTimeKST = TimeZoneUtils.convert(reservation.getStartTime(), UTC, KST);
        LocalDateTime reservationEndTimeKST = TimeZoneUtils.convert(reservation.getEndTime(), UTC, KST);

        return new SlackResponse(
                reservation.getSpace().getMap().getName(),
                reservation.getSpace().getName(),
                reservation.getUserName(),
                reservationStartTimeKST,
                reservationEndTimeKST,
                reservation.getDescription(),
                sharingMapId,
                slackUrl);
    }

    @Override
    public String toString() {
        return spaceName + "\\n " +
                userName + "\\n " +
                reservationTime + "\\n " +
                description;
    }
}
