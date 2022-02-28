package com.woowacourse.zzimkkong.domain;

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
    }

    public boolean isNotDivisibleBy(final Minute timeUnitMinute) {
        Minute startMinute = Minute.from(startTime.getMinute());
        Minute endMinute = Minute.from(endTime.getMinute());

        return !(startMinute.isDivisibleBy(timeUnitMinute) && endMinute.isDivisibleBy(timeUnitMinute));
    }

    public boolean isDurationShorterThan(final Minute timeUnitMinute) {
        return getDurationMinute().isShorterThan(timeUnitMinute);
    }

    public boolean isDurationLongerThan(final Minute timeUnitMinute) {
        return timeUnitMinute.isShorterThan(getDurationMinute());
    }

    public boolean isNotWithin(final TimeSlot that) {
        boolean isEqualOrAfterStartTime = this.startTime.equals(that.startTime) || this.startTime.isAfter(that.startTime);
        boolean isEqualOrBeforeEndTime = this.endTime.equals(that.endTime) || this.endTime.isBefore(that.endTime);
        return !(isEqualOrAfterStartTime && isEqualOrBeforeEndTime);
    }

    private Minute getDurationMinute() {
        return Minute.from(ChronoUnit.MINUTES.between(startTime, endTime));
    }
}

