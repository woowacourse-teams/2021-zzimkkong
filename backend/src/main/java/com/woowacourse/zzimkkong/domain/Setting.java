package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.space.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@NoArgsConstructor
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

    @Column(nullable = false)
    private String enabledDayOfWeek;

    protected Setting(
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

        validateSetting();
    }

    private void validateSetting() {
        if (availableStartTime.equals(availableEndTime) || availableStartTime.isAfter(availableEndTime)) {
            throw new ImpossibleAvailableStartEndTimeException();
        }

        if (isAvailableTimeAndTimeUnitMismatch()) {
            throw new TimeUnitMismatchException();
        }

        if (reservationMaximumTimeUnit < reservationMinimumTimeUnit) {
            throw new InvalidMinimumMaximumTimeUnitException();
        }

        if (isTimeUnitInconsistent()) {
            throw new TimeUnitInconsistencyException();
        }

        int duration = (int) ChronoUnit.MINUTES.between(availableStartTime, availableEndTime);
        if (duration < reservationMaximumTimeUnit) {
            throw new NotEnoughAvailableTimeException();
        }
    }

    private boolean isAvailableTimeAndTimeUnitMismatch() {
        return availableStartTime.getMinute() % reservationTimeUnit != 0 || availableEndTime.getMinute() % reservationTimeUnit != 0;
    }

    public boolean checkTimeByTimeUnit(final int minute) {
        return minute % this.reservationTimeUnit != 0;
    }

    private boolean isTimeUnitInconsistent() {
        return !(isMinimumTimeUnitConsistentWithTimeUnit() && isMaximumTimeUnitConsistentWithTimeUnit());
    }

    private boolean isMinimumTimeUnitConsistentWithTimeUnit() {
        int minimumTimeUnitQuotient = reservationMinimumTimeUnit / reservationTimeUnit;
        int minimumTimeUnitRemainder = reservationMinimumTimeUnit % reservationTimeUnit;
        return minimumTimeUnitRemainder == 0 && 1 <= minimumTimeUnitQuotient;
    }

    private boolean isMaximumTimeUnitConsistentWithTimeUnit() {
        int maximumTimeUnitRemainder = reservationMaximumTimeUnit % reservationTimeUnit;
        return maximumTimeUnitRemainder == 0;
    }
}
