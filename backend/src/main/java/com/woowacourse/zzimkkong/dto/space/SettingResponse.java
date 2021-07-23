package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Space;

import java.time.LocalTime;

public class SettingResponse {
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
    private Integer reservationTimeUnit;
    private Integer reservationMinimumTimeUnit;
    private Integer reservationMaximumTimeUnit;
    private Boolean reservationEnable;
    private String disabledWeekdays;

    public SettingResponse() {
    }

    private SettingResponse(
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final String disabledWeekdays) {
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.reservationEnable = reservationEnable;
        this.disabledWeekdays = disabledWeekdays;
    }

    public static SettingResponse from(final Space space) {
        return new SettingResponse(
                space.getAvailableStartTime(),
                space.getAvailableEndTime(),
                space.getReservationTimeUnit(),
                space.getReservationMinimumTimeUnit(),
                space.getReservationMaximumTimeUnit(),
                space.getReservationEnable(),
                space.getDisabledWeekdays()
        );
    }
}
