package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.IllegalTimeUnitValueException;
import com.woowacourse.zzimkkong.exception.reservation.ImpossibleStartEndTimeException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
@Embeddable
public class TimeSlot {
    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;

    private TimeSlot(
            final LocalTime startTime,
            final LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        validateStartEndTime(startTime, endTime);
    }

    public static TimeSlot of(final LocalTime startTime, final LocalTime endTime) {
        return new TimeSlot(startTime, endTime);
    }

    public static void validateStartEndTime(final LocalTime startTime, final LocalTime endTime) {
        if (endTime.isBefore(startTime) || startTime.equals(endTime)) {
            throw new ImpossibleStartEndTimeException();
        }

        if (TimeUnit.cannotDivideByMinimumTimeUnit(startTime.getMinute())) {
            throw new IllegalTimeUnitValueException(startTime.getMinute());
        }

        if (TimeUnit.cannotDivideByMinimumTimeUnit(endTime.getMinute())) {
            throw new IllegalTimeUnitValueException(endTime.getMinute());
        }
    }

    public boolean isNotDivisibleBy(final TimeUnit timeUnit) {
        return !(timeUnit.canDivide(startTime) && timeUnit.canDivide(endTime));
    }

    public boolean isDurationShorterThan(final TimeUnit timeUnit) {
        return getDurationMinute().isShorterThan(timeUnit);
    }

    public boolean isDurationLongerThan(final TimeUnit timeUnit) {
        return timeUnit.isShorterThan(getDurationMinute());
    }

    public boolean isNotWithin(final TimeSlot that) {
        boolean isEqualOrAfterStartTime = this.startTime.equals(that.startTime) || this.startTime.isAfter(that.startTime);
        boolean isEqualOrBeforeEndTime = this.endTime.equals(that.endTime) || this.endTime.isBefore(that.endTime);
        return !(isEqualOrAfterStartTime && isEqualOrBeforeEndTime);
    }

    private TimeUnit getDurationMinute() {
        return TimeUnit.from(ChronoUnit.MINUTES.between(startTime, endTime));
    }
}

