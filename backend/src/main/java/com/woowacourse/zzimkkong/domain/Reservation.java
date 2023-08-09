package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.setting.NoMatchingSettingException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "i_spaceid_date", columnList = "space_id, date"),
        @Index(name = "i_memberid_date", columnList = "member_id, date")})
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ReservationTime reservationTime;

    @Column(length = 4)
    private String password;

    @Column(nullable = false, length = 20)
    private String userName;

    @Column(nullable = false, length = 100)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "fk_reservation_space"), nullable = false)
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_reservation_member"))
    private Member member;

    protected Reservation(
            final Long id,
            final ReservationTime reservationTime,
            final String password,
            final String userName,
            final String description,
            final Space space,
            final Member member) {
        this.id = id;
        this.reservationTime = reservationTime;
        this.password = password;
        this.userName = userName;
        this.description = description;
        this.space = space;
        this.member = member;

        if (space != null) {
            this.space.addReservation(this);
        }
    }

    public boolean hasConflictWith(final Reservation that) {
        return this.reservationTime.hasConflictWith(that.reservationTime);
    }

    public boolean isWrongPassword(final String thatPassword) {
        if (this.password == null || thatPassword == null) {
            return true;
        }
        return !this.password.equals(thatPassword);
    }

    public void update(final Reservation updateReservation) {
        this.reservationTime = updateReservation.reservationTime;
        this.member = updateReservation.member;
        this.userName = updateReservation.userName;
        this.description = updateReservation.description;
        this.space = updateReservation.space;
    }

    public void updateReservationTime(final ReservationTime updateReservationTime) {
        this.reservationTime = updateReservationTime;
    }

    public LocalDateTime getStartTime() {
        return reservationTime.getStartTime();
    }

    public LocalDateTime getEndTime() {
        return reservationTime.getEndTime();
    }

    public boolean isInUse(final LocalDateTime now) {
        return reservationTime.contains(now);
    }

    public boolean isExpired(final LocalDateTime now) {
        return reservationTime.isBefore(now);
    }

    public ServiceZone getServiceZone() {
        return space.getServiceZone();
    }

    public LocalDate getDate() {
        return reservationTime.getDate();
    }

    public TimeSlot getTimeSlot() {
        return reservationTime.at(space.getServiceZone());
    }

    public DayOfWeek getDayOfWeek() {
        return reservationTime.getDayOfWeek();
    }

    public Boolean isOwnedBy(final Member loginUser) {
        return !isNotOwnedBy(loginUser);
    }

    public boolean isNotOwnedBy(final Member thatMember) {
        if (member == null || thatMember == null) {
            return true;
        }
        return !this.member.equals(thatMember);
    }

    public boolean hasMember() {
        return this.member != null;
    }

    public Long getMemberId() {
        if (!hasMember()) {
            return null;
        }
        return this.member.getId();
    }

    public String getUserName() {
        if (hasMember()) {
            return this.member.getUserName();
        }
        return this.userName;
    }

    public TimeUnit getReservationTimeUnit() {
        Setting setting = space.getSpaceSettings().getSettings()
                .stream()
                .filter(it -> it.supports(getTimeSlot(), getDayOfWeek()))
                .findFirst()
                .orElseThrow(NoMatchingSettingException::new);

        return setting.getReservationTimeUnit();
    }
}
