package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.InvalidConferenceTimeException;
import com.woowacourse.zzimkkong.exception.InvalidTimeUnitException;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@DynamicInsert
@DynamicUpdate
@Entity
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    // TODO: map Editor 구현되면 column 삭제
    @Column(nullable = true, length = 6)
    private String textPosition;

    @Column(nullable = true, length = 25)
    private String color;

    @Column(nullable = true)
    private String coordinate;

    @ManyToOne
    @JoinColumn(name = "map_id", foreignKey = @ForeignKey(name = "fk_space_map"), nullable = false)
    private Map map;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String area;

    @Embedded
    private Setting setting;

    @Column(nullable = false)
    private String mapImage;    // todo Map 엔티티의 mapImageUrl 과 중복되는 칼럼이므로 삭제

    protected Space() {
    }

    protected Space(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.textPosition = builder.textPosition;
        this.color = builder.color;
        this.coordinate = builder.coordinate;
        this.map = builder.map;
        this.description = builder.description;
        this.area = builder.area;
        this.setting = builder.setting;
        this.mapImage = builder.mapImage;
    }

    public void update(final Space updateSpace) {
        this.name = updateSpace.name;
        this.map = updateSpace.map;
        this.description = updateSpace.description;
        this.area = updateSpace.area;
        this.setting = updateSpace.setting;
        this.mapImage = updateSpace.mapImage;
    }

    public void validateTimeUnit(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        long durationMinutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime);

        validateReservationTimeUnit(startDateTime.getMinute(), durationMinutes);
        validateMinimumMaximumTimeUnit(durationMinutes);
    }

    private void validateMinimumMaximumTimeUnit(long durationMinutes) {
        if(durationMinutes < getReservationMinimumTimeUnit() || durationMinutes > getReservationMaximumTimeUnit()) {
            throw new InvalidConferenceTimeException();
        }
    }

    private void validateReservationTimeUnit(int minute, long durationMinutes) {
        if(minute != 0 && minute % this.getReservationTimeUnit() != 0) {
            throw new InvalidTimeUnitException();
        }
        if (durationMinutes % this.getReservationTimeUnit() != 0) {
            throw new InvalidTimeUnitException();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTextPosition() {
        return textPosition;
    }

    public String getColor() {
        return color;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public Map getMap() {
        return map;
    }

    public String getDescription() {
        return description;
    }

    public String getArea() {
        return area;
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

    public String getDisabledWeekdays() {
        return setting.getDisabledWeekdays();
    }

    public String getMapImage() {
        return mapImage;
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
        private String mapImage = null;

        public Builder() {
        }

        public Space.Builder id(Long inputId) {
            id = inputId;
            return this;
        }

        public Space.Builder name(String inputName) {
            name = inputName;
            return this;
        }

        public Space.Builder textPosition(String inputTextPosition) {
            textPosition = inputTextPosition;
            return this;
        }

        public Space.Builder color(String inputColor) {
            color = inputColor;
            return this;
        }

        public Space.Builder coordinate(String inputCoordinate) {
            coordinate = inputCoordinate;
            return this;
        }

        public Space.Builder map(Map inputMap) {
            map = inputMap;
            return this;
        }

        public Space.Builder description(String inputDescription) {
            description = inputDescription;
            return this;
        }

        public Space.Builder area(String inputArea) {
            area = inputArea;
            return this;
        }

        public Space.Builder setting(Setting inputSetting) {
            setting = inputSetting;
            return this;
        }

        public Space.Builder mapImage(String inputMapImage) {
            mapImage = inputMapImage;
            return this;
        }

        public Space build() {
            return new Space(this);
        }
    }
}
