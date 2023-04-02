package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.IllegalTimeUnitValueException;
import com.woowacourse.zzimkkong.exception.reservation.ImpossibleStartEndTimeException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TimeSlot {
    public static final LocalTime MAX_TIME = LocalTime.of(23, 50);

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private TimeSlot(
            final LocalTime startTime,
            final LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        validateStartEndTime(this.startTime, this.endTime);
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

    public boolean contains(final TimeSlot that) {
        boolean equalOrAfterStartTime = that.startTime.equals(this.startTime) || that.startTime.isAfter(this.startTime);
        boolean equalOrBeforeEndTime = that.endTime.equals(this.endTime) || that.endTime.isBefore(this.endTime);
        return equalOrAfterStartTime && equalOrBeforeEndTime;
    }

    public boolean isNotWithin(final TimeSlot that) {
        boolean equalOrAfterStartTime = this.startTime.equals(that.startTime) || this.startTime.isAfter(that.startTime);
        boolean equalOrBeforeEndTime = this.endTime.equals(that.endTime) || this.endTime.isBefore(that.endTime);
        return !(equalOrAfterStartTime && equalOrBeforeEndTime);
    }

    public boolean isExtendableWith(final TimeSlot that) {
        return this.endTime.equals(that.startTime);
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

    private TimeUnit getDurationMinute() {
        return TimeUnit.from(ChronoUnit.MINUTES.between(startTime, endTime));
    }

    @Override
    public String toString() {
        String endTimeAsString = endTime.toString();
        if (MAX_TIME.equals(endTime)) {
            endTimeAsString = "24:00";
        }

        return startTime + " ~ " + endTimeAsString;
    }

    public List<TimeSlot> extractExclusiveTimeSlots(final TimeSlot that) {
        if (!this.hasConflictWith(that)) {
            return List.of(this);
        }

        if (that.contains(this)) {
            return Collections.emptyList();
        }

        if (this.contains(that)) {
            return List.of(
                    TimeSlot.of(this.startTime, that.startTime),
                    TimeSlot.of(that.endTime, this.endTime));
        }

        if (this.hasLeftSkewedConflictWith(that)) {
            return List.of(TimeSlot.of(this.startTime, that.startTime));
        }

        return List.of(TimeSlot.of(that.endTime, this.endTime));
    }

    private boolean hasLeftSkewedConflictWith(final TimeSlot that) {
        return this.startTime.isBefore(that.startTime) && this.endTime.isAfter(that.startTime) && this.endTime.isBefore(that.endTime);
    }
}

