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

    // TODO: Build 하는 곳에서 EnabledDayOfWeek.toString() 해주기
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

        if (isNoneMatchingAvailableTimeAndTimeUnit()) {
            throw new TimeUnitMismatchException();
        }

        if (reservationMaximumTimeUnit < reservationMinimumTimeUnit) {
            throw new InvalidMinimumMaximumTimeUnitException();
        }

        if (isNotConsistentTimeUnit()) {
            throw new TimeUnitInconsistencyException();
        }

        int duration = (int) ChronoUnit.MINUTES.between(availableStartTime, availableEndTime);
        if (duration < reservationMaximumTimeUnit) {
            throw new NotEnoughAvailableTimeException();
        }
    }

    private boolean isNoneMatchingAvailableTimeAndTimeUnit() {
        return isNotDivisibleByTimeUnit(availableStartTime.getMinute()) || isNotDivisibleByTimeUnit(availableEndTime.getMinute());
    }

    public boolean isNotDivisibleByTimeUnit(final int minute) {
        return minute % this.reservationTimeUnit != 0;
    }

    private boolean isNotConsistentTimeUnit() {
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
