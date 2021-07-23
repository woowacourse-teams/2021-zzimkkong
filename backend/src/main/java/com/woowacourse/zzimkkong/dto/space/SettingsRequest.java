package com.woowacourse.zzimkkong.dto.space;

import java.time.LocalTime;

public class SettingsRequest {
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
    private Integer reservationTimeUnit;
    private Integer reservationMinimumTimeUnit;
    private Integer reservationMaximumTimeUnit;
    private Boolean reservationEnable;
    private String disabledWeekdays;

    public SettingsRequest() {
    }

    public SettingsRequest(
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

    public LocalTime getAvailableStartTime() {
        return availableStartTime;
    }

    public LocalTime getAvailableEndTime() {
        return availableEndTime;
    }

    public Integer getReservationTimeUnit() {
        return reservationTimeUnit;
    }

    public Integer getReservationMinimumTimeUnit() {
        return reservationMinimumTimeUnit;
    }

    public Integer getReservationMaximumTimeUnit() {
        return reservationMaximumTimeUnit;
    }

    public Boolean getReservationEnable() {
        return reservationEnable;
    }

    public String getDisabledWeekdays() {
        return disabledWeekdays;
    }
}
