package com.woowacourse.zzimkkong.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;

@Embeddable
public class Setting {
    @Column(nullable = false)
    private LocalTime availableStartTime;

    @Column(nullable = false)
    private LocalTime availableEndTime;

    @Column(nullable = false)
    private Integer reservationTimeUnit;

    @Column(nullable = false)
    private Integer reservationMinimumTimeUnit;

    @Column(nullable = false)
    private Integer reservationMaximumTimeUnit;

    @Column(nullable = false)
    private Boolean reservationEnable;

    @Column(nullable = true)
    private String enabledDayOfWeek;

    public Setting() {
    }

    protected Setting(final Builder builder) {
        this.availableStartTime = builder.availableStartTime;
        this.availableEndTime = builder.availableEndTime;
        this.reservationTimeUnit = builder.reservationTimeUnit;
        this.reservationMinimumTimeUnit = builder.reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = builder.reservationMaximumTimeUnit;
        this.reservationEnable = builder.reservationEnable;
        this.enabledDayOfWeek = builder.enabledDayOfWeek;
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

    public static class Builder {
        private LocalTime availableStartTime = null;
        private LocalTime availableEndTime = null;
        private Integer reservationTimeUnit = null;
        private Integer reservationMinimumTimeUnit = null;
        private Integer reservationMaximumTimeUnit = null;
        private Boolean reservationEnable = null;
        private String enabledDayOfWeek = null;

        public Builder() {
        }

        public Setting.Builder availableStartTime(final LocalTime inputAvailableStartTime) {
            availableStartTime = inputAvailableStartTime;
            return this;
        }

        public Setting.Builder availableEndTime(final LocalTime inputAvailableEndTime) {
            availableEndTime = inputAvailableEndTime;
            return this;
        }

        public Setting.Builder reservationTimeUnit(final Integer inputReservationTimeUnit) {
            reservationTimeUnit = inputReservationTimeUnit;
            return this;
        }

        public Setting.Builder reservationMinimumTimeUnit(final Integer inputReservationMinimumTimeUnit) {
            reservationMinimumTimeUnit = inputReservationMinimumTimeUnit;
            return this;
        }

        public Setting.Builder reservationMaximumTimeUnit(final Integer inputReservationMaximumTimeUnit) {
            reservationMaximumTimeUnit = inputReservationMaximumTimeUnit;
            return this;
        }

        public Setting.Builder reservationEnable(final Boolean inputReservationEnable) {
            reservationEnable = inputReservationEnable;
            return this;
        }

        public Setting.Builder enabledDayOfWeek(final String inputEnabledDayOfWeek) {
            enabledDayOfWeek = inputEnabledDayOfWeek;
            return this;
        }

        public Setting build() {
            return new Setting(this);
        }
    }
}
