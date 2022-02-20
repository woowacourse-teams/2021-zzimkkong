package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.space.InvalidMinimumMaximumTimeUnitException;
import com.woowacourse.zzimkkong.exception.space.NotEnoughAvailableTimeException;
import com.woowacourse.zzimkkong.exception.space.TimeUnitInconsistencyException;
import com.woowacourse.zzimkkong.exception.space.TimeUnitMismatchException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Builder
@Embeddable
public class Setting {
    @Embedded
    private TimeSlot availableTimeSlot;

    @Embedded
    private Minute reservationTimeUnit;

    @Embedded
    private Minute reservationMinimumTimeUnit;

    @Embedded
    private Minute reservationMaximumTimeUnit;

    @Column(nullable = false)
    private Boolean reservationEnable;

    @Column(nullable = false)
    private String enabledDayOfWeek;

    protected Setting(
            final TimeSlot availableTimeSlot,
            final Minute reservationTimeUnit,
            final Minute reservationMinimumTimeUnit,
            final Minute reservationMaximumTimeUnit,
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
        return reservationTimeUnit.getMinute();
    }

    public Integer getReservationMinimumTimeUnitAsInt() {
        return reservationMinimumTimeUnit.getMinute();
    }

    public Integer getReservationMaximumTimeUnitAsInt() {
        return reservationMaximumTimeUnit.getMinute();
    }

    private boolean isNotConsistentTimeUnit() {
        return !(reservationMinimumTimeUnit.isDivisibleBy(reservationTimeUnit) &&
                reservationMaximumTimeUnit.isDivisibleBy(reservationTimeUnit));
    }
}
