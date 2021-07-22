package com.woowacourse.zzimkkong.dto.space;

import java.time.LocalDateTime;
import java.util.List;

public class SettingsRequest {
    private LocalDateTime availableStartTime;
    private LocalDateTime availableEndTime;
    private Integer reservationTimeUnit;
    private Integer reservationMinimumTimeUnit;
    private Integer reservationMaximumTimeUnit;
    private Boolean reservationEnable;
    private String disabledWeekdays;

    public SettingsRequest() {
    }

    public SettingsRequest(
            final LocalDateTime availableStartTime,
            final LocalDateTime availableEndTime,
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

    public LocalDateTime getAvailableStartTime() {
        return availableStartTime;
    }

    public LocalDateTime getAvailableEndTime() {
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
