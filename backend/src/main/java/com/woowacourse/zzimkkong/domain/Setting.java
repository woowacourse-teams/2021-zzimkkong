package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.space.InvalidMinimumMaximumTimeUnitException;
import com.woowacourse.zzimkkong.exception.space.NotEnoughAvailableTimeException;
import com.woowacourse.zzimkkong.exception.space.TimeUnitInconsistencyException;
import com.woowacourse.zzimkkong.exception.space.TimeUnitMismatchException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class Setting {
    @Embedded
    private TimeSlot availableTimeSlot;

    @Embedded
    private TimeUnit reservationTimeUnit;

    @Embedded
    private TimeUnit reservationMinimumTimeUnit;

    @Embedded
    private TimeUnit reservationMaximumTimeUnit;

    @Column(nullable = false)
    private Boolean reservationEnable;

    @Column(nullable = false)
    private String enabledDayOfWeek;

    protected Setting(
            final TimeSlot availableTimeSlot,
            final TimeUnit reservationTimeUnit,
            final TimeUnit reservationMinimumTimeUnit,
            final TimeUnit reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final String enabledDayOfWeek) {
        this.availableTimeSlot = availableTimeSlot;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.reservationEnable = reservationEnable;
        this.enabledDayOfWeek = enabledDayOfWeek;

        validateSetting();
    }

    private void validateSetting() {
        if (availableTimeSlot.isNotDivisibleBy(reservationTimeUnit)) {
            throw new TimeUnitMismatchException();
        }

        if (reservationMaximumTimeUnit.isShorterThan(reservationMinimumTimeUnit)) {
            throw new InvalidMinimumMaximumTimeUnitException();
        }

        if (isNotConsistentTimeUnit()) {
            throw new TimeUnitInconsistencyException();
        }

        if (availableTimeSlot.isDurationShorterThan(reservationMaximumTimeUnit)) {
            throw new NotEnoughAvailableTimeException();
        }
    }

    public boolean cannotDivideByTimeUnit(final TimeSlot timeSlot) {
        return timeSlot.isNotDivisibleBy(reservationTimeUnit);
    }

    public boolean hasLongerMinimumTimeUnitThan(final TimeSlot timeSlot) {
        return timeSlot.isDurationShorterThan(reservationMinimumTimeUnit);
    }

    public boolean hasShorterMaximumTimeUnitThan(final TimeSlot timeSlot) {
        return timeSlot.isDurationLongerThan(reservationMaximumTimeUnit);
    }

    public boolean hasNotEnoughAvailableTimeToCover(final TimeSlot timeSlot) {
        return timeSlot.isNotWithin(availableTimeSlot);
    }

    public LocalTime getAvailableStartTime() {
        return availableTimeSlot.getStartTime();
    }

    public LocalTime getAvailableEndTime() {
        return availableTimeSlot.getEndTime();
    }

    public Integer getReservationTimeUnitAsInt() {
        return reservationTimeUnit.getMinutes();
    }

    public Integer getReservationMinimumTimeUnitAsInt() {
        return reservationMinimumTimeUnit.getMinutes();
    }

    public Integer getReservationMaximumTimeUnitAsInt() {
        return reservationMaximumTimeUnit.getMinutes();
    }

    private boolean isNotConsistentTimeUnit() {
        return !(reservationMinimumTimeUnit.isDivisibleBy(reservationTimeUnit) &&
                reservationMaximumTimeUnit.isDivisibleBy(reservationTimeUnit));
    }
}
