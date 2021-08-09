package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.space.NoSuchDayOfWeekException;
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

    // TODO: map Editor 구현되면 column 삭제
    @Column(nullable = true, length = 6)
    private String textPosition;

    @Column(nullable = true)
    private String coordinate;

    @ManyToOne
    @JoinColumn(name = "map_id", foreignKey = @ForeignKey(name = "fk_space_map"), nullable = false)
    private Map map;

    protected Space() {
    }

    protected Space(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.color = builder.color;
        this.description = builder.description;
        this.area = builder.area;
        this.setting = builder.setting;
        this.textPosition = builder.textPosition;
        this.coordinate = builder.coordinate;
        this.map = builder.map;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getArea() {
        return area;
    }

    public String getTextPosition() {
        return textPosition;
    }

    public String getCoordinate() {
        return coordinate;
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

    public Map getMap() {
        return map;
    }

    public static class Builder {
        private Long id = null;
        private String name = null;
        private String textPosition = null;
        private String color = null;
        private String coordinate = null;
        private Map map = null;
        private String description = null;
        private String area = null;
        private Setting setting = null;

        public Builder() {
        }

        public Space.Builder id(final Long inputId) {
            id = inputId;
            return this;
        }

        public Space.Builder name(final String inputName) {
            name = inputName;
            return this;
        }

        public Space.Builder color(final String inputColor) {
            color = inputColor;
            return this;
        }

        public Space.Builder description(final String inputDescription) {
            description = inputDescription;
            return this;
        }

        public Space.Builder area(final String inputArea) {
            area = inputArea;
            return this;
        }

        public Space.Builder setting(final Setting inputSetting) {
            setting = inputSetting;
            return this;
        }

        public Space.Builder textPosition(final String inputTextPosition) {
            textPosition = inputTextPosition;
            return this;
        }

        public Space.Builder coordinate(final String inputCoordinate) {
            coordinate = inputCoordinate;
            return this;
        }

        public Space.Builder map(final Map inputMap) {
            map = inputMap;
            return this;
        }

        public Space build() {
            return new Space(this);
        }
    }
}
