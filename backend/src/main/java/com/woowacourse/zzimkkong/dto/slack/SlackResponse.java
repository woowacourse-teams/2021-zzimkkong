package com.woowacourse.zzimkkong.dto.slack;

import com.woowacourse.zzimkkong.domain.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SlackResponse {
    private String spaceName;
    private String userName;
    private String reservationTime;
    private String description;
    private String sharingMapId;

    private SlackResponse(
            final String spaceName,
            final String userName,
            final LocalDateTime startTime,
            final LocalDateTime endTime,
            final String description,
            final String sharingMapId) {
        this.spaceName = "회의실명 : " + spaceName;
        this.userName = "예약자명 : " + userName;
        this.reservationTime = "예약시간 : " + startTime + " ~ " + endTime;
        this.description = "예약내용 : " + description;
        this.sharingMapId = sharingMapId;
    }

    public static SlackResponse of(final Reservation reservation, final String sharingMapId) {
        return new SlackResponse(
                reservation.getSpace().getName(),
                reservation.getUserName(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getDescription(),
                sharingMapId);
    }

    @Override
    public String toString() {
        return spaceName + "\\n " +
                userName + "\\n " +
                reservationTime + "\\n " +
                description;
    }
}
