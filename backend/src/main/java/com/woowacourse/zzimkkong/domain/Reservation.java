package com.woowacourse.zzimkkong.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 20)
    private String userName;

    @Column(nullable = false, length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "fk_reservation_space"), nullable = false)
    private Space space;

    protected Reservation() {
    }

    protected Reservation(final Builder builder) {
        this.id = builder.id;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.password = builder.password;
        this.userName = builder.userName;
        this.description = builder.description;
        this.space = builder.space;
    }

    public boolean hasConflictWith(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        boolean contains = contains(startDateTime, endDateTime);
        boolean intersects = intersects(startDateTime, endDateTime);
        boolean equals = equals(startDateTime, endDateTime);

        return contains || intersects || equals;
    }

    private boolean contains(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        return (startDateTime.isAfter(startTime) && endDateTime.isBefore(endTime))
                || (startDateTime.isEqual(startTime) && endDateTime.isBefore(endTime))
                || (startDateTime.isAfter(startTime) && endDateTime.isEqual(endTime));
    }

    private boolean intersects(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        return (startDateTime.isBefore(startTime) && endDateTime.isAfter(startTime))
                || (endDateTime.isAfter(endTime) && startDateTime.isBefore(endTime));
    }

    private boolean equals(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        return startDateTime.isEqual(startTime) && endDateTime.isEqual(endTime);
    }

    public void update(final Reservation updateReservation, final Space space) {
        this.startTime = updateReservation.startTime;
        this.endTime = updateReservation.endTime;
        this.userName = updateReservation.userName;
        this.description = updateReservation.description;
        this.space = space;
    }

    public static class Builder {
        private Long id = null;
        private LocalDateTime startTime = null;
        private LocalDateTime endTime = null;
        private String password = null;
        private String userName = null;
        private String description = null;
        private Space space = null;

        public Builder() {
        }

        public Builder id(final Long inputId) {
            id = inputId;
            return this;
        }

        public Builder startTime(final LocalDateTime inputStartTime) {
            startTime = inputStartTime.truncatedTo(ChronoUnit.SECONDS);
            return this;
        }

        public Builder endTime(final LocalDateTime inputEndTime) {
            endTime = inputEndTime.truncatedTo(ChronoUnit.SECONDS);
            return this;
        }

        public Builder password(final String inputPassword) {
            password = inputPassword;
            return this;
        }

        public Builder userName(final String inputUserName) {
            userName = inputUserName;
            return this;
        }

        public Builder description(final String inputDescription) {
            description = inputDescription;
            return this;
        }

        public Builder space(final Space inputSpace) {
            space = inputSpace;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }

    }

    public boolean isWrongPassword(final String password) {
        return !this.password.equals(password);
    }
}
