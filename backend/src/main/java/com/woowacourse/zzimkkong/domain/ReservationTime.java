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

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ReservationTime {
    /**
     * The reservation date at the service zone (time zone) of the map
     * Indexing purpose
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
            final ServiceZone serviceZone,
            final boolean manageable) {
        LocalDateTime reservationStartDateTime = startDateTime.withSecond(0).withNano(0);
        LocalDateTime reservationEndDateTime = endDateTime.withSecond(0).withNano(0);
        validateReservationTime(reservationStartDateTime, reservationEndDateTime, serviceZone, manageable);

        LocalDate date = TimeZoneUtils.convertTo(reservationStartDateTime, serviceZone).toLocalDate();
        return new ReservationTime(date, reservationStartDateTime, reservationEndDateTime);
    }

    public static ReservationTime ofDefaultServiceZone(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        return of(startDateTime, endDateTime, ServiceZone.KOREA, true);
    }

    private static void validatePastTime(final LocalDateTime startDateTime) {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new PastReservationTimeException();
        }
    }

    private static void validateReservationTime(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final ServiceZone serviceZone,
            final boolean manageable) {
        LocalDateTime convertedStartDateTime = TimeZoneUtils.convertTo(startDateTime, serviceZone);
        LocalDateTime convertedEndDateTime = TimeZoneUtils.convertTo(endDateTime, serviceZone);

        if (!manageable) {
            validatePastTime(startDateTime);
        }
        validateStartEndDate(convertedStartDateTime, convertedEndDateTime);
        TimeSlot.validateStartEndTime(convertedStartDateTime.toLocalTime(), convertedEndDateTime.toLocalTime());
    }

    private static void validateStartEndDate(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        if (!startDate.isEqual(endDate)) {
            throw new NonMatchingStartEndDateException();
        }
    }

    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    public TimeSlot at(ServiceZone serviceZone) {
        LocalDateTime convertedStartDateTime = TimeZoneUtils.convertTo(startTime, serviceZone);
        LocalDateTime convertedEndDateTime = TimeZoneUtils.convertTo(endTime, serviceZone);

        return TimeSlot.of(convertedStartDateTime.toLocalTime(), convertedEndDateTime.toLocalTime());
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