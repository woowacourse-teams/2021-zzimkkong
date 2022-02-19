package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.reservation.ImpossibleStartTimeException;
import com.woowacourse.zzimkkong.exception.reservation.NonMatchingStartEndDateException;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.KST;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
public class ReservationTime {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final LocalDate dateKST;
    private final TimeSlot timeSlotKST;

    private ReservationTime(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final boolean managerFlag) {
        this.startDateTime = startDateTime.withSecond(0).withNano(0);
        this.endDateTime = endDateTime.withSecond(0).withNano(0);
        LocalDateTime startDateTimeKST = TimeZoneUtils.convert(this.startDateTime, UTC, KST);
        LocalDateTime endDateTimeKST = TimeZoneUtils.convert(this.endDateTime, UTC, KST);

        if (!managerFlag) {
            validatePastTime();
        }
        validateStartEndDate(startDateTimeKST, endDateTimeKST);

        this.dateKST = startDateTimeKST.toLocalDate();
        this.timeSlotKST = TimeSlot.of(startDateTimeKST.toLocalTime(), endDateTimeKST.toLocalTime());
    }

    public static ReservationTime of(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final boolean managerFlag) {
        return new ReservationTime(startDateTime, endDateTime, managerFlag);
    }

    public static ReservationTime of(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime) {
        return new ReservationTime(startDateTime, endDateTime, true);
    }

    public DayOfWeek getDayOfWeekKST() {
        return dateKST.getDayOfWeek();
    }

    private void validatePastTime() {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new ImpossibleStartTimeException();
        }
    }

    private void validateStartEndDate(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        if (!startDate.isEqual(endDate)) {
            throw new NonMatchingStartEndDateException();
        }
    }

    public boolean hasConflictWith(final ReservationTime thatReservationTime) {
        return this.dateKST.equals(thatReservationTime.dateKST) &&
                this.timeSlotKST.hasConflictWith(thatReservationTime.timeSlotKST);
    }
}