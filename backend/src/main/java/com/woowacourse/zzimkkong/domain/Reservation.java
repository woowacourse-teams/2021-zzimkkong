package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "fk_reservation_space"))
    private Space space;

    protected Reservation() {
    }

    protected Reservation(Builder builder) {
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

    public boolean hasSameData(final Reservation updatedReservation) {
        return this.startTime.equals(updatedReservation.startTime)
                && this.endTime.equals(updatedReservation.endTime)
                && this.userName.equals(updatedReservation.userName)
                && this.description.equals(updatedReservation.description)
                && this.space.equals(updatedReservation.space);
    }

    public void update(final ReservationCreateUpdateRequest reservationCreateUpdateRequest, final Space space) {
        this.startTime = reservationCreateUpdateRequest.getStartDateTime();
        this.endTime = reservationCreateUpdateRequest.getEndDateTime();
        this.userName = reservationCreateUpdateRequest.getName();
        this.description = reservationCreateUpdateRequest.getDescription();
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

        public Builder id(Long inputId) {
            id = inputId;
            return this;
        }

        public Builder startTime(LocalDateTime inputStartTime) {
            startTime = inputStartTime.truncatedTo(ChronoUnit.SECONDS);
            return this;
        }

        public Builder endTime(LocalDateTime inputEndTime) {
            endTime = inputEndTime.truncatedTo(ChronoUnit.SECONDS);
            return this;
        }

        public Builder password(String inputPassword) {
            password = inputPassword;
            return this;
        }

        public Builder userName(String inputUserName) {
            userName = inputUserName;
            return this;
        }

        public Builder description(String inputDescription) {
            description = inputDescription;
            return this;
        }

        public Builder space(Space inputSpace) {
            space = inputSpace;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }

    }

    public boolean isWrongPassword(String password) {
        return !this.password.equals(password);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }

    public String getUserName() {
        return userName;
    }

    public Space getSpace() {
        return space;
    }
}
