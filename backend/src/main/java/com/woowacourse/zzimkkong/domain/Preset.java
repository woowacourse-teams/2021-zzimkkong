package com.woowacourse.zzimkkong.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Builder
@Getter
@NoArgsConstructor
@Entity
public class Preset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_preset_member"), nullable = false)
    private Member member;

    public Preset(
            final String name,
            final TimeSlot settingTimeSlot,
            final TimeUnit reservationTimeUnit,
            final TimeUnit reservationMinimumTimeUnit,
            final TimeUnit reservationMaximumTimeUnit,
            final String enabledDayOfWeek,
            final Member member) {
        this.name = name;
        this.settingTimeSlot = settingTimeSlot;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.enabledDayOfWeek = enabledDayOfWeek;
        this.member = member;

        if (this.member != null) {
            this.member.addPreset(this);
        }
    }

    public Preset(
            final Long id,
            final String name,
            final TimeSlot settingTimeSlot,
            final TimeUnit reservationTimeUnit,
            final TimeUnit reservationMinimumTimeUnit,
            final TimeUnit reservationMaximumTimeUnit,
            final String enabledDayOfWeek,
            final Member member) {
        this(name, settingTimeSlot, reservationTimeUnit, reservationMinimumTimeUnit, reservationMaximumTimeUnit, enabledDayOfWeek, member);
        this.id = id;
    }

    public boolean isNotOwnedBy(final Member member) {
        return this.member.equals(member);
    }

    public boolean hasSameId(final Long presetId) {
        return this.id.equals(presetId);
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
}
