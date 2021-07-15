package com.woowacourse.zzimkkong.dto.slack;

import com.woowacourse.zzimkkong.domain.Reservation;

import java.time.LocalDateTime;

public class Contents {
    private String spaceName;
    private String userName;
    private String reservationTime;
    private String description;

    public Contents() {
    }

    private Contents(String spaceName, String userName, LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.spaceName = "회의실명 : " + spaceName;
        this.userName = "예약자명 : " + userName;
        this.reservationTime = "예약시간 : " + startTime + " ~ " + endTime;
        this.description = "예약내용 : " + description;
    }

    public static Contents from(Reservation reservation) {
        return new Contents(
                reservation.getSpace().getName(),
                reservation.getUserName(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getDescription());
    }

    @Override
    public String toString() {
        return spaceName + "\\n " +
                userName + "\\n " +
                reservationTime + "\\n " +
                description;
    }
}
