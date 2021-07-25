package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;

import javax.persistence.*;
import java.time.LocalTime;

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
    @JoinColumn(name = "map_id", foreignKey = @ForeignKey(name = "fk_space_map"))
    private Map map;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String area;

    @Column(nullable = true)
    private LocalTime availableStartTime;

    @Column(nullable = true)
    private LocalTime availableEndTime;

    @Column(nullable = true)
    private Integer reservationTimeUnit;

    @Column(nullable = true)
    private Integer reservationMinimumTimeUnit;

    @Column(nullable = true)
    private Integer reservationMaximumTimeUnit;

    @Column(nullable = true)
    private Boolean reservationEnable;

    @Column(nullable = true)
    private String disabledWeekdays;

    @Column(nullable = true)
    private String mapImage;

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
        this.availableStartTime = builder.availableStartTime;
        this.availableEndTime = builder.availableEndTime;
        this.reservationTimeUnit = builder.reservationTimeUnit;
        this.reservationMinimumTimeUnit = builder.reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = builder.reservationMaximumTimeUnit;
        this.reservationEnable = builder.reservationEnable;
        this.disabledWeekdays = builder.disabledWeekdays;
        this.mapImage = builder.mapImage;
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

    public LocalTime getAvailableStartTime() {
        return availableStartTime;
    }

    public LocalTime getAvailableEndTime() {
        return availableEndTime;
    }

    public Integer getReservationTimeUnit() {
        return reservationTimeUnit;
    }

    public Integer getReservationMinimumTimeUnit() {
        return reservationMinimumTimeUnit;
    }

    public Integer getReservationMaximumTimeUnit() {
        return reservationMaximumTimeUnit;
    }

    public Boolean getReservationEnable() {
        return reservationEnable;
    }

    public String getDisabledWeekdays() {
        return disabledWeekdays;
    }

    public String getMapImage() {
        return mapImage;
    }

    public boolean hasSameData(final Space updatedSpace) {
        return this.id.equals(updatedSpace.id)
                && this.name.equals(updatedSpace.name)
                && this.textPosition.equals(updatedSpace.textPosition)
                && this.color.equals(updatedSpace.color)
                && this.coordinate.equals(updatedSpace.coordinate)
                && this.map.equals(updatedSpace.map)
                && this.description.equals(updatedSpace.description)
                && this.area.equals(updatedSpace.area)
                && this.availableStartTime.equals(updatedSpace.availableStartTime)
                && this.availableEndTime.equals(updatedSpace.availableEndTime)
                && this.reservationTimeUnit.equals(updatedSpace.reservationTimeUnit)
                && this.reservationMinimumTimeUnit.equals(updatedSpace.reservationMinimumTimeUnit)
                && this.reservationMaximumTimeUnit.equals(updatedSpace.reservationMaximumTimeUnit)
                && this.reservationEnable.equals(updatedSpace.reservationEnable)
                && this.disabledWeekdays.equals(updatedSpace.disabledWeekdays)
                && this.mapImage.equals(updatedSpace.mapImage);
    }

    public void update(final SpaceCreateUpdateRequest spaceCreateUpdateRequest, final Map map) {
        this.name = spaceCreateUpdateRequest.getName();
        this.map = map;
        this.description = spaceCreateUpdateRequest.getDescription();
        this.area = spaceCreateUpdateRequest.getArea();

        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();
        this.availableStartTime = settingsRequest.getAvailableStartTime();
        this.availableEndTime = settingsRequest.getAvailableEndTime();
        this.reservationTimeUnit = settingsRequest.getReservationTimeUnit();
        this.reservationMinimumTimeUnit = settingsRequest.getReservationMinimumTimeUnit();
        this.reservationMaximumTimeUnit = settingsRequest.getReservationMaximumTimeUnit();
        this.reservationEnable = settingsRequest.getReservationEnable();
        this.disabledWeekdays = settingsRequest.getDisabledWeekdays();

        this.mapImage = spaceCreateUpdateRequest.getMapImage();
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
        private LocalTime availableStartTime = null;
        private LocalTime availableEndTime = null;
        private Integer reservationTimeUnit = null;
        private Integer reservationMinimumTimeUnit = null;
        private Integer reservationMaximumTimeUnit = null;
        private Boolean reservationEnable = null;
        private String disabledWeekdays = null;
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

        public Space.Builder availableStartTime(LocalTime inputAvailableStartTime) {
            availableStartTime = inputAvailableStartTime;
            return this;
        }

        public Space.Builder availableEndTime(LocalTime inputAvailableEndTime) {
            availableEndTime = inputAvailableEndTime;
            return this;
        }

        public Space.Builder reservationTimeUnit(Integer inputReservationTimeUnit) {
            reservationTimeUnit = inputReservationTimeUnit;
            return this;
        }

        public Space.Builder reservationMinimumTimeUnit(Integer inputReservationMinimumTimeUnit) {
            reservationMinimumTimeUnit = inputReservationMinimumTimeUnit;
            return this;
        }

        public Space.Builder reservationMaximumTimeUnit(Integer inputReservationMaximumTimeUnit) {
            reservationMaximumTimeUnit = inputReservationMaximumTimeUnit;
            return this;
        }

        public Space.Builder reservationEnable(Boolean inputReservationEnable) {
            reservationEnable = inputReservationEnable;
            return this;
        }

        public Space.Builder disabledWeekdays(String inputDisabledWeekdays) {
            disabledWeekdays = inputDisabledWeekdays;
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
