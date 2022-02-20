package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.ImpossibleStartTimeException;
import com.woowacourse.zzimkkong.exception.reservation.NonMatchingStartEndDateException;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.KST;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
@NoArgsConstructor
@Builder
@Embeddable
public class ReservationTime {
    private LocalDate date;
    @Embedded
    private TimeSlot timeSlot;

    protected ReservationTime(final LocalDate date, final TimeSlot timeSlot) {
        this.date = date;
        this.timeSlot = timeSlot;
    }

    public static ReservationTime of(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final boolean managerFlag) {
        LocalDateTime reservationStartDateTime = startDateTime.withSecond(0).withNano(0);
        LocalDateTime reservationEndDateTime = endDateTime.withSecond(0).withNano(0);
        validateReservationTime(reservationStartDateTime, reservationEndDateTime, managerFlag);

        LocalDate date = reservationStartDateTime.toLocalDate();
        TimeSlot timeSlot = TimeSlot.of(reservationStartDateTime.toLocalTime(), reservationEndDateTime.toLocalTime());

        return new ReservationTime(date, timeSlot);
    }

    public static ReservationTime of(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime) {
        return of(startDateTime, endDateTime, true);
    }

    private static void validateReservationTime(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final boolean managerFlag) {
        LocalDateTime startDateTimeKST = TimeZoneUtils.convert(startDateTime, UTC, KST);
        LocalDateTime endDateTimeKST = TimeZoneUtils.convert(endDateTime, UTC, KST);

        if (!managerFlag) {
            validatePastTime(startDateTime);
        }
        validateStartEndDate(startDateTimeKST, endDateTimeKST);
    }

    private static void validatePastTime(final LocalDateTime startDateTime) {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new ImpossibleStartTimeException();
        }
    }

    private static void validateStartEndDate(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        if (!startDate.isEqual(endDate)) {
            throw new NonMatchingStartEndDateException();
        }
    }

    public DayOfWeek getDayOfWeekKST() {
        return getDateKST().getDayOfWeek();
    }

    public boolean hasConflictWith(final ReservationTime thatReservationTime) {
        LocalDate thisDateKST = getDateKST();
        TimeSlot thisTimeSlotKST = getTimeSlotKST();

        LocalDate thatDateKST = thatReservationTime.getDateKST();
        TimeSlot thatTimeSlotKST = thatReservationTime.getTimeSlotKST();

        return thisDateKST.equals(thatDateKST) &&
                thisTimeSlotKST.hasConflictWith(thatTimeSlotKST);
    }

    public LocalDate getDateKST() {
        return getStartDateTimeKST().toLocalDate();
    }

    public TimeSlot getTimeSlotKST() {
        LocalDateTime startDateTimeKST = getStartDateTimeKST();
        LocalDateTime endDateTimeKST = getEndDateTimeKST();

        return TimeSlot.of(startDateTimeKST.toLocalTime(), endDateTimeKST.toLocalTime());
    }

    private LocalDateTime getStartDateTimeKST() {
        LocalDateTime startDateTime = date.atTime(timeSlot.getStartTime());
        return TimeZoneUtils.convert(startDateTime, UTC, KST);
    }

    private LocalDateTime getEndDateTimeKST() {
        LocalDateTime endDateTime = date.atTime(timeSlot.getEndTime());
        return TimeZoneUtils.convert(endDateTime, UTC, KST);
    }
}