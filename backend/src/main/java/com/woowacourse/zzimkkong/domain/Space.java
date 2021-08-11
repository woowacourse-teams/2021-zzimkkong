package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.space.NoSuchDayOfWeekException;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@DynamicInsert
@DynamicUpdate
@Entity
public class Space {
    public static final String DELIMITER = ",";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = true, length = 25)
    private String color;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String area;

    @Embedded
    private Setting setting;

    @ManyToOne
    @JoinColumn(name = "map_id", foreignKey = @ForeignKey(name = "fk_space_map"), nullable = false)
    private Map map;

    protected Space() {
    }

    protected Space(
            final Long id,
            final String name,
            final String color,
            final String description,
            final String area,
            final Setting setting,
            final Map map) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.area = area;
        this.setting = setting;
        this.map = map;

        if (map != null) {
            map.addSpace(this);
        }
    }

    public void update(final Space updateSpace) {
        this.name = updateSpace.name;
        this.color = updateSpace.color;
        this.description = updateSpace.description;
        this.area = updateSpace.area;
        this.setting = updateSpace.setting;
    }

    public boolean isNotBetweenAvailableTime(final LocalDateTime startDateTime, LocalDateTime endDateTime) {
        boolean isEqualOrAfterStartTime = startDateTime.toLocalTime().equals(getAvailableStartTime()) ||
                startDateTime.toLocalTime().isAfter(getAvailableStartTime());
        boolean isEqualOrBeforeEndTime = endDateTime.toLocalTime().equals(getAvailableEndTime()) ||
                endDateTime.toLocalTime().isBefore(getAvailableEndTime());
        return !(isEqualOrAfterStartTime && isEqualOrBeforeEndTime);
    }

    public boolean isIncorrectTimeUnit(final int minute) {
        return minute != 0 && isNotDivideBy(minute);
    }

    public boolean isIncorrectMinimumMaximumTimeUnit(final int durationMinutes) {
        return durationMinutes < getReservationMinimumTimeUnit() || durationMinutes > getReservationMaximumTimeUnit();
    }

    public boolean isNotDivideBy(final int minute) {
        return minute % getReservationTimeUnit() != 0;
    }

    public boolean isUnableToReserve() {
        return !getReservationEnable();
    }

    public boolean isClosedOn(final DayOfWeek dayOfWeek) {
        return getEnabledDaysOfWeek().stream()
                .noneMatch(enabledDayOfWeek -> enabledDayOfWeek.equals(dayOfWeek));
    }

    private List<DayOfWeek> getEnabledDaysOfWeek() {
        String enabledDayOfWeekNames = getEnabledDayOfWeek();

        if (enabledDayOfWeekNames == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(enabledDayOfWeekNames.split(DELIMITER))
                .map(String::trim)
                .map(this::convertToDayOfWeek)
                .collect(Collectors.toList());
    }

    private DayOfWeek convertToDayOfWeek(final String dayOfWeekName) {
        return Arrays.stream(DayOfWeek.values())
                .filter(dayOfWeek -> dayOfWeek.name().equals(dayOfWeekName.toUpperCase()))
                .findAny()
                .orElseThrow(NoSuchDayOfWeekException::new);
    }

    public boolean hasSameId(final Long spaceId) {
        return id.equals(spaceId);
    }

    public LocalTime getAvailableEndTime() {
        return setting.getAvailableEndTime();
    }

    public LocalTime getAvailableStartTime() {
        return setting.getAvailableStartTime();
    }

    public Integer getReservationTimeUnit() {
        return setting.getReservationTimeUnit();
    }

    public Integer getReservationMinimumTimeUnit() {
        return setting.getReservationMinimumTimeUnit();
    }

    public Integer getReservationMaximumTimeUnit() {
        return setting.getReservationMaximumTimeUnit();
    }

    public Boolean getReservationEnable() {
        return setting.getReservationEnable();
    }

    public String getEnabledDayOfWeek() {
        return setting.getEnabledDayOfWeek();
    }
}
