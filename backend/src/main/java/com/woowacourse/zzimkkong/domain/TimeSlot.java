package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.ImpossibleEndTimeException;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TimeSlot {
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Minute durationMinute;

    private TimeSlot(
            final LocalTime startTime,
            final LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        validateStartEndTime();

        this.durationMinute = Minute.from(ChronoUnit.MINUTES.between(this.startTime, this.endTime));
    }

    public static TimeSlot of(final LocalTime startTime, final LocalTime endTime) {
        return new TimeSlot(startTime, endTime);
    }

    public boolean isNotDivisibleBy(final Minute timeUnitMinute) {
        Minute startMinute = Minute.from(startTime.getMinute());
        return durationMinute.isNotDivisibleBy(timeUnitMinute) || startMinute.isNotDivisibleBy(timeUnitMinute);
    }

    public boolean isDurationShorterThan(final Minute timeUnitMinute) {
        return durationMinute.isShorterThan(timeUnitMinute);
    }

    public boolean isDurationLongerThan(final Minute timeUnitMinute) {
        return timeUnitMinute.isShorterThan(durationMinute);
    }

    public boolean isNotWithin(final TimeSlot that) {
        boolean isEqualOrAfterStartTime = this.startTime.equals(that.startTime) || this.startTime.isAfter(that.startTime);
        boolean isEqualOrBeforeEndTime = this.endTime.equals(that.endTime) || this.endTime.isBefore(that.endTime);
        return !(isEqualOrAfterStartTime && isEqualOrBeforeEndTime);
    }

    public boolean hasConflictWith(final TimeSlot that) {
        return !(this.isEarlierThan(that) || this.isLaterThan(that));
    }

    private boolean isEarlierThan(final TimeSlot that) {
        return this.endTime.equals(that.startTime) || this.endTime.isBefore(that.startTime);
    }

    private boolean isLaterThan(final TimeSlot that) {
        return this.startTime.equals(that.endTime) || this.startTime.isAfter(that.endTime);
    }

    private void validateStartEndTime() {
        if (this.endTime.isBefore(this.startTime) || this.startTime.equals(this.endTime)) {
            throw new ImpossibleEndTimeException();
        }
    }
}
