package com.woowacourse.zzimkkong.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
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

    protected Reservation(Long id, LocalDateTime startTime, LocalDateTime endTime, String password, String userName, String description, Space space) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.password = password;
        this.userName = userName;
        this.description = description;
        this.space = space;
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

    public boolean isWrongPassword(final String password) {
        return !this.password.equals(password);
    }
}
