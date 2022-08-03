package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.NonMatchingStartEndDateException;
import com.woowacourse.zzimkkong.exception.reservation.PastReservationTimeException;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.KST;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ReservationTime {
    /**
     * The reservation date in KST (the time zone of the map)
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * The reservation start date time in UTC
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * The reservation end date time in UTC
     */
    @Column(nullable = false)
    private LocalDateTime endTime;

    private ReservationTime(final LocalDate date, final LocalDateTime startTime, final LocalDateTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ReservationTime of(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final boolean manageable) {
        LocalDateTime reservationStartDateTime = startDateTime.withSecond(0).withNano(0);
        LocalDateTime reservationEndDateTime = endDateTime.withSecond(0).withNano(0);
        validateReservationTime(reservationStartDateTime, reservationEndDateTime, manageable);

        LocalDate date = TimeZoneUtils.convert(reservationStartDateTime, UTC, KST).toLocalDate();
        return new ReservationTime(date, reservationStartDateTime, reservationEndDateTime);
    }

    public static ReservationTime of(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime) {
        return of(startDateTime, endDateTime, true);
    }

    private static void validatePastTime(final LocalDateTime startDateTime) {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new PastReservationTimeException();
        }
    }

    private static void validateReservationTime(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final boolean manageable) {
        LocalDateTime startDateTimeKST = TimeZoneUtils.convert(startDateTime, UTC, KST);
        LocalDateTime endDateTimeKST = TimeZoneUtils.convert(endDateTime, UTC, KST);

        if (!manageable) {
            validatePastTime(startDateTime);
        }
        validateStartEndDate(startDateTimeKST, endDateTimeKST);
        TimeSlot.validateStartEndTime(startDateTimeKST.toLocalTime(), endDateTimeKST.toLocalTime());
    }

    private static void validateStartEndDate(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        if (!startDate.isEqual(endDate)) {
            throw new NonMatchingStartEndDateException();
        }
    }

    public DayOfWeek getDayOfWeekKST() {
        return date.getDayOfWeek();
    }

    public TimeSlot asTimeSlotKST() {
        LocalDateTime startDateTimeKST = TimeZoneUtils.convert(startTime, UTC, KST);
        LocalDateTime endDateTimeKST = TimeZoneUtils.convert(endTime, UTC, KST);

        return TimeSlot.of(startDateTimeKST.toLocalTime(), endDateTimeKST.toLocalTime());
    }

    public boolean contains(final LocalDateTime now) {
        return !(startTime.isAfter(now) || endTime.isBefore(now));
    }

    public boolean isBefore(final LocalDateTime now) {
        return endTime.isBefore(now);
    }

    public boolean hasConflictWith(final ReservationTime that) {
        return !(this.isEarlierThan(that) || this.isLaterThan(that));
    }

    private boolean isEarlierThan(final ReservationTime that) {
        return this.endTime.equals(that.startTime) || this.endTime.isBefore(that.startTime);
    }

    private boolean isLaterThan(final ReservationTime that) {
        return this.startTime.equals(that.endTime) || this.startTime.isAfter(that.endTime);
    }
}