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

    // TODO: ENUMERATED 데이터 타입으로 변경하기
    @Column(nullable = true)
    private String disabledDayOfWeek;

    public Setting() {
    }

    protected Setting(Builder builder) {
        this.availableStartTime = builder.availableStartTime;
        this.availableEndTime = builder.availableEndTime;
        this.reservationTimeUnit = builder.reservationTimeUnit;
        this.reservationMinimumTimeUnit = builder.reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = builder.reservationMaximumTimeUnit;
        this.reservationEnable = builder.reservationEnable;
        this.disabledDayOfWeek = builder.disabledWeekdays;
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

    public String getDisabledDayOfWeek() {
        return disabledDayOfWeek;
    }

    public static class Builder {
        private LocalTime availableStartTime = null;
        private LocalTime availableEndTime = null;
        private Integer reservationTimeUnit = null;
        private Integer reservationMinimumTimeUnit = null;
        private Integer reservationMaximumTimeUnit = null;
        private Boolean reservationEnable = null;
        private String disabledWeekdays = null;

        public Builder() {
        }

        public Setting.Builder availableStartTime(LocalTime inputAvailableStartTime) {
            availableStartTime = inputAvailableStartTime;
            return this;
        }

        public Setting.Builder availableEndTime(LocalTime inputAvailableEndTime) {
            availableEndTime = inputAvailableEndTime;
            return this;
        }

        public Setting.Builder reservationTimeUnit(Integer inputReservationTimeUnit) {
            reservationTimeUnit = inputReservationTimeUnit;
            return this;
        }

        public Setting.Builder reservationMinimumTimeUnit(Integer inputReservationMinimumTimeUnit) {
            reservationMinimumTimeUnit = inputReservationMinimumTimeUnit;
            return this;
        }

        public Setting.Builder reservationMaximumTimeUnit(Integer inputReservationMaximumTimeUnit) {
            reservationMaximumTimeUnit = inputReservationMaximumTimeUnit;
            return this;
        }

        public Setting.Builder reservationEnable(Boolean inputReservationEnable) {
            reservationEnable = inputReservationEnable;
            return this;
        }

        public Setting.Builder disabledWeekdays(String inputDisabledWeekdays) {
            disabledWeekdays = inputDisabledWeekdays;
            return this;
        }

        public Setting build() {
            return new Setting(this);
        }
    }
}
