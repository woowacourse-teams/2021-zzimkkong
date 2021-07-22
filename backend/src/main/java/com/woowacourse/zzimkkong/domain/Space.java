package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

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
        this.reservationMinimumTimeUnit = builder.reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = builder.reservationMaximumTimeUnit;
        this.reservationEnable = builder.reservationEnable;
        this.disabledWeekdays = builder.disabledWeekdays;
        this.mapImage = builder.mapImage;
    }

    public Space(final String name,
                 final String textPosition,
                 final String color,
                 final String coordinate,
                 final Map map) {
        this(null, name, textPosition, color, coordinate, map, null, null, null, null, null, null, null, null, null);
    }

    public Space(
            final Long id,
            final String name,
            final String textPosition,
            final String color,
            final String coordinate,
            final Map map,
            final String description,
            final String area,
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final String disabledWeekdays,
            final String mapImage) {
        this.id = id;
        this.name = name;
        this.textPosition = textPosition;
        this.color = color;
        this.coordinate = coordinate;
        this.map = map;
        this.description = description;
        this.area = area;
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.reservationEnable = reservationEnable;
        this.disabledWeekdays = disabledWeekdays;
        this.mapImage = mapImage;
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
        private Integer reservationMinimumTimeUnit = null;
        private Integer reservationMaximumTimeUnit = null;
        private Boolean reservationEnable = null;
        private String disabledWeekdays = null;
        private String mapImage;

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
            availableStartTime = inputAvailableStartTime.truncatedTo(ChronoUnit.SECONDS);
            return this;
        }

        public Space.Builder availableEndTime(LocalTime inputAvailableEndTime) {
            availableEndTime = inputAvailableEndTime.truncatedTo(ChronoUnit.SECONDS);
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

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
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
}
