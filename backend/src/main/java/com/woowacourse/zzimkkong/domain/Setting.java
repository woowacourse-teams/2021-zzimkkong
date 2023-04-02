package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.setting.InvalidOrderException;
import com.woowacourse.zzimkkong.exception.space.InvalidMinimumMaximumTimeUnitException;
import com.woowacourse.zzimkkong.exception.space.NotEnoughAvailableTimeException;
import com.woowacourse.zzimkkong.exception.space.TimeUnitInconsistencyException;
import com.woowacourse.zzimkkong.exception.space.TimeUnitMismatchException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NEGATIVE_SETTING_ORDER_MESSAGE;
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
    private Integer order;

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
            final Integer order,
            final Space space) {
        this.id = id;
        this.settingTimeSlot = settingTimeSlot;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.enabledDayOfWeek = enabledDayOfWeek;
        this.order = order;
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

        if (order == null || order <= 0) {
            throw new InvalidOrderException(NEGATIVE_SETTING_ORDER_MESSAGE);
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
        List<EnabledDayOfWeek> thisEnabledDayOfWeek = this.getEnabledDayOfWeekList();
        boolean enabledDayOfWeekMatch = that.getEnabledDayOfWeekList().stream().anyMatch(thisEnabledDayOfWeek::contains);

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

    public List<EnabledDayOfWeek> getEnabledDayOfWeekList() {
        return Arrays.stream(this.enabledDayOfWeek.split(Space.DELIMITER))
                .map(String::trim)
                .map(EnabledDayOfWeek::from)
                .collect(Collectors.toList());
    }

    /**
     * 2023.04.02 기준
     * 인자로 주어진 settings 의 조건들에 배타적인 (겹치지 않는) 새로운 setting slot 리스트를 생성한다
     * 기존 setting 을 조각내어 새로운 여러개의 (transient) setting 을 생성한다
     *
     * @param settings 서로 시간대와 요일이 겹치지 않는 (= flat 한) setting 들
     * @return List of Setting Slot (조각 내어진 세팅 슬롯들)
     */

    public List<Setting> extractExclusiveSettingSlots(final List<Setting> settings) {
        List<Setting> exclusiveSettingSlots = List.of(this);
        for (Setting setting : settings) {
            List<Setting> newExclusiveSettingSlots = new ArrayList<>();
            for (Setting exclusiveSettingSlot : exclusiveSettingSlots) {
                newExclusiveSettingSlots.addAll(exclusiveSettingSlot.extractNewExclusiveSettingSlots(setting));
            }
            exclusiveSettingSlots = newExclusiveSettingSlots;
        }
        return exclusiveSettingSlots;
    }

    private List<Setting> extractNewExclusiveSettingSlots(final Setting setting) {
        if (!this.hasConflictWith(setting)) {
            return List.of(this);
        }

        List<Setting> newExclusiveSettingSlots = new ArrayList<>();

        List<EnabledDayOfWeek> conflictingSettingEnabledDayOfWeek = setting.getEnabledDayOfWeekList();
        Map<Boolean, List<EnabledDayOfWeek>> splitEnabledDayOfWeek = this.getEnabledDayOfWeekList()
                .stream()
                .collect(Collectors.partitioningBy(conflictingSettingEnabledDayOfWeek::contains));

        if (!CollectionUtils.isEmpty(splitEnabledDayOfWeek.get(false))) {
            String nonConflictingDayOfWeek = splitEnabledDayOfWeek.get(false).stream()
                    .map(dayOfWeek -> dayOfWeek.name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining());
            Setting intactSettingSlot = Setting.builder()
                    .id(0L)
                    .settingTimeSlot(this.settingTimeSlot)
                    .reservationTimeUnit(this.reservationTimeUnit)
                    .reservationMinimumTimeUnit(this.reservationMinimumTimeUnit)
                    .reservationMaximumTimeUnit(this.reservationMaximumTimeUnit)
                    .enabledDayOfWeek(nonConflictingDayOfWeek)
                    .order(0)
                    .space(this.space)
                    .build();
            newExclusiveSettingSlots.add(intactSettingSlot);
        }

        if (!CollectionUtils.isEmpty(splitEnabledDayOfWeek.get(true))) {
            String conflictingDayOfWeek = splitEnabledDayOfWeek.get(true).stream()
                    .map(dayOfWeek -> dayOfWeek.name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining());
            List<TimeSlot> exclusiveTimeSlots = this.settingTimeSlot.extractExclusiveTimeSlots(setting.settingTimeSlot);
            exclusiveTimeSlots.stream()
                    .map(exclusiveTimeSlot -> Setting.builder()
                            .id(0L)
                            .settingTimeSlot(exclusiveTimeSlot)
                            .reservationTimeUnit(this.reservationTimeUnit)
                            .reservationMinimumTimeUnit(this.reservationMinimumTimeUnit)
                            .reservationMaximumTimeUnit(this.reservationMaximumTimeUnit)
                            .enabledDayOfWeek(conflictingDayOfWeek)
                            .order(0)
                            .space(this.space)
                            .build())
                    .forEach(newExclusiveSettingSlots::add);
        }

        return newExclusiveSettingSlots;
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

    public String toSummaryWithoutDayOfWeek(final Boolean flat) {
        String priority = "[우선순위 " + order.toString() + "] ";
        if (flat) {
            priority = StringUtils.EMPTY;
        }
        return String.format("%s%s (최소 %s, 최대 %s, 예약 단위 %s)",
                priority,
                settingTimeSlot.toString(),
                reservationMinimumTimeUnit.toString(),
                reservationMaximumTimeUnit.toString(),
                reservationTimeUnit.toString());
    }
}
