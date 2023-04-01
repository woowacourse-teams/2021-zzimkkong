package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.setting.InvalidPriorityException;
import com.woowacourse.zzimkkong.exception.space.InvalidMinimumMaximumTimeUnitException;
import com.woowacourse.zzimkkong.exception.space.NotEnoughAvailableTimeException;
import com.woowacourse.zzimkkong.exception.space.TimeUnitInconsistencyException;
import com.woowacourse.zzimkkong.exception.space.TimeUnitMismatchException;
import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.woowacourse.zzimkkong.infrastructure.message.MessageUtils.LINE_SEPARATOR;

@Builder
@Getter
@NoArgsConstructor
@Entity
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startTime",
                    column = @Column(name = "setting_start_time")),
            @AttributeOverride(name = "endTime",
                    column = @Column(name = "setting_end_time")),
    })
    private TimeSlot settingTimeSlot;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "minutes",
                    column = @Column(name = "reservation_time_unit"))
    })
    private TimeUnit reservationTimeUnit;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "minutes",
                    column = @Column(name = "reservation_minimum_time_unit"))
    })
    private TimeUnit reservationMinimumTimeUnit;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "minutes",
                    column = @Column(name = "reservation_maximum_time_unit"))
    })
    private TimeUnit reservationMaximumTimeUnit;

    @Column(nullable = false)
    private String enabledDayOfWeek;

    @Column(nullable = false)
    private Integer priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "fk_setting_space"), nullable = false)
    private Space space;

    public Setting(
            final Long id,
            final TimeSlot settingTimeSlot,
            final TimeUnit reservationTimeUnit,
            final TimeUnit reservationMinimumTimeUnit,
            final TimeUnit reservationMaximumTimeUnit,
            final String enabledDayOfWeek,
            final Integer priority,
            final Space space) {
        this.id = id;
        this.settingTimeSlot = settingTimeSlot;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.enabledDayOfWeek = enabledDayOfWeek;
        this.priority = priority;
        this.space = space;

        validateSetting();
    }

    private void validateSetting() {
        if (settingTimeSlot.isNotDivisibleBy(reservationTimeUnit)) {
            throw new TimeUnitMismatchException();
        }

        if (reservationMaximumTimeUnit.isShorterThan(reservationMinimumTimeUnit)) {
            throw new InvalidMinimumMaximumTimeUnitException();
        }

        if (isNotConsistentTimeUnit()) {
            throw new TimeUnitInconsistencyException();
        }

        if (settingTimeSlot.isDurationShorterThan(reservationMaximumTimeUnit)) {
            throw new NotEnoughAvailableTimeException();
        }

        if (priority == null || priority <= 0) {
            throw new InvalidPriorityException();
        }
    }

    public LocalTime getSettingStartTime() {
        return settingTimeSlot.getStartTime();
    }

    public LocalTime getSettingEndTime() {
        return settingTimeSlot.getEndTime();
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

    public boolean hasConflictWith(final Setting that) {
        List<EnabledDayOfWeek> thisEnabledDayOfWeek = Arrays.stream(this.enabledDayOfWeek.split(Space.DELIMITER))
                .map(String::trim)
                .map(EnabledDayOfWeek::from)
                .collect(Collectors.toList());
        boolean enabledDayOfWeekMatch = Arrays.stream(that.enabledDayOfWeek.split(Space.DELIMITER))
                .map(String::trim)
                .map(EnabledDayOfWeek::from)
                .anyMatch(thisEnabledDayOfWeek::contains);

        return this.settingTimeSlot.hasConflictWith(that.settingTimeSlot) && enabledDayOfWeekMatch;
    }

    public boolean supports(final TimeSlot timeSlot, final DayOfWeek dayOfWeek) {
        return enabledDayOfWeek.contains(dayOfWeek.name().toLowerCase())
                && settingTimeSlot.hasConflictWith(timeSlot);
    }

    public boolean cannotAcceptDueToTimeUnit(final TimeSlot timeSlot) {
        return timeSlot.isNotDivisibleBy(reservationTimeUnit);
    }

    public boolean cannotAcceptDueToMinimumTimeUnit(final TimeSlot timeSlot) {
        return timeSlot.isDurationShorterThan(reservationMinimumTimeUnit);
    }

    public boolean cannotAcceptDueToMaximumTimeUnit(final TimeSlot timeSlot) {
        return timeSlot.isDurationLongerThan(reservationMaximumTimeUnit);
    }

    public void updateSpace(final Space space) {
        this.space = space;
    }

    @Override
    public String toString() {
        return "예약 가능한 요일: " +
                EnabledDayOfWeek.getDisplayNames(enabledDayOfWeek) +
                LINE_SEPARATOR +
                "예약 가능한 시간대: " +
                settingTimeSlot.toString() +
                LINE_SEPARATOR +
                "예약 시간 단위: " +
                reservationTimeUnit.toString() +
                LINE_SEPARATOR +
                "최소 예약 가능 시간: " +
                reservationMinimumTimeUnit.toString() +
                LINE_SEPARATOR +
                "최대 예약 가능 시간: " +
                reservationMaximumTimeUnit.toString() +
                LINE_SEPARATOR;
    }
}
