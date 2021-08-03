package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingsRequest {
    private LocalTime availableStartTime = LocalTime.of(0, 0);
    private LocalTime availableEndTime = LocalTime.of(23, 59);
    private Integer reservationTimeUnit = 10;
    private Integer reservationMinimumTimeUnit = 10;
    private Integer reservationMaximumTimeUnit = 1440;
    private Boolean reservationEnable = true;
    private String enabledDayOfWeek = "monday, tuesday, wednesday, thursday, friday, saturday, sunday";

    public SettingsRequest() {
    }

    public SettingsRequest(
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final String enabledDayOfWeek) {
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.reservationEnable = reservationEnable;
        this.enabledDayOfWeek = enabledDayOfWeek;
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

    public String getEnabledDayOfWeek() {
        return enabledDayOfWeek;
    }
}
