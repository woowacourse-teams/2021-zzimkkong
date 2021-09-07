package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.space.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@NoArgsConstructor
@Embeddable
public class Setting {
    @Column(nullable = false)
    private LocalTime availableStartTime;

    @Column(nullable = false)
    private LocalTime availableEndTime;

    @Column(nullable = false)
    private Integer reservationTimeUnit;

    @Column(nullable = false)
    private Integer reservationMinimumTimeUnit;

    @Column(nullable = false)
    private Integer reservationMaximumTimeUnit;

    @Column(nullable = false)
    private Boolean reservationEnable;

    @Column(nullable = false)
    private String enabledDayOfWeek;

    protected Setting(
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final String enabledDayOfWeek) {
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.reservationEnable = reservationEnable;
        this.enabledDayOfWeek = enabledDayOfWeek;

        if (this.availableStartTime.equals(this.availableEndTime) || this.availableStartTime.isAfter(this.availableEndTime)) {
            throw new ImpossibleAvailableStartEndTimeException();
        }

        // TODO: 10분 으로 마무리 지어져야함 (edge case: 10:15 ~ 11:35는 가능함)
        if (this.availableStartTime.getMinute() % this.reservationTimeUnit != 0 || this.availableEndTime.getMinute() % this.reservationTimeUnit != 0) {
            throw new TimeUnitMismatchException();
        }

        if (this.reservationMaximumTimeUnit < this.reservationMinimumTimeUnit) {
            throw new InvalidMinimumMaximumTimeUnitException();
        }

        final int minimumTimeUnitQuotient = this.reservationMinimumTimeUnit / this.reservationTimeUnit;
        final int minimumTimeUnitRemainder = this.reservationMinimumTimeUnit % this.reservationTimeUnit;

        final int maximumTimeUnitQuotient = this.reservationMaximumTimeUnit / this.reservationTimeUnit;
        final int maximumTimeUnitRemainder = this.reservationMaximumTimeUnit % this.reservationTimeUnit;

        if (!((minimumTimeUnitRemainder == 0 && 1 <= minimumTimeUnitQuotient) && (maximumTimeUnitRemainder == 0 && 1 <= maximumTimeUnitQuotient))) {
            throw new TimeUnitInconsistencyException();
        }

        int duration = (int) ChronoUnit.MINUTES.between(this.availableStartTime, this.availableEndTime);
        if (duration < this.reservationMaximumTimeUnit) {
            throw new NotEnoughAvailableTimeException();
        }
    }
}
