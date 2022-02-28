package com.woowacourse.zzimkkong.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@Entity
@Table(indexes = @Index(name = "i_spaceid_date", columnList = "space_id, date"))
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ReservationTime reservationTime;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 20)
    private String userName;

    @Column(nullable = false, length = 100)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "fk_reservation_space"), nullable = false)
    private Space space;

    protected Reservation(
            final Long id,
            final ReservationTime reservationTime,
            final String password,
            final String userName,
            final String description,
            final Space space) {
        this.id = id;
        this.reservationTime = reservationTime;
        this.password = password;
        this.userName = userName;
        this.description = description;
        this.space = space;

        if (space != null) {
            this.space.addReservation(this);
        }
    }

    public boolean hasConflictWith(final ReservationTime thatReservationTime) {
        return this.reservationTime.hasConflictWith(thatReservationTime);
    }

    public boolean isWrongPassword(final String password) {
        return !this.password.equals(password);
    }

    public void update(final Reservation updateReservation, final Space space) {
        this.reservationTime = updateReservation.getReservationTime();
        this.userName = updateReservation.userName;
        this.description = updateReservation.description;
        this.space = space;
    }

    public LocalDateTime getStartTime() {
        return reservationTime.getStartTime();
    }

    public LocalDateTime getEndTime() {
        return reservationTime.getEndTime();
    }
}
