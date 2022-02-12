package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
@Builder
@NoArgsConstructor
@Entity
@Table(indexes = @Index(name = "i_spaceid_date", columnList = "space_id, date"))
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "fk_reservation_space"), nullable = false)
    private Space space;

    protected Reservation(
            final Long id,
            final LocalDate date,
            final LocalDateTime startTime,
            final LocalDateTime endTime,
            final String password,
            final String userName,
            final String description,
            final Space space) {
        this.id = id;
        this.date = date;
        this.startTime = startTime.withSecond(0).withNano(0);
        this.endTime = endTime.withSecond(0).withNano(0);
        this.password = password;
        this.userName = userName;
        this.description = description;
        this.space = space;

        if (space != null) {
            this.space.addReservation(this);
        }
    }

    public boolean hasConflictWith(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        return !(isEarlier(endDateTime) || isLater(startDateTime));
    }

    private boolean isEarlier(final LocalDateTime endDateTime) {
        return endDateTime.equals(this.startTime) || endDateTime.isBefore(this.startTime);
    }

    private boolean isLater(final LocalDateTime startDateTime) {
        return startDateTime.equals(this.endTime) || startDateTime.isAfter(this.endTime);
    }

    public void update(final Reservation updateReservation, final Space space) {
        this.date = updateReservation.date;
        this.startTime = updateReservation.startTime;
        this.endTime = updateReservation.endTime;
        this.userName = updateReservation.userName;
        this.description = updateReservation.description;
        this.space = space;
    }

    public boolean isWrongPassword(final String password) {
        return !this.password.equals(password);
    }

    public boolean isBookedOn(LocalDate date, TimeZone timeZone) {
        LocalDate convertedStartTimeDate = TimeZoneUtils.convert(startTime, UTC, timeZone).toLocalDate();
        LocalDate convertedEndTimeDate = TimeZoneUtils.convert(endTime, UTC, timeZone).toLocalDate();
        return date.equals(convertedStartTimeDate) && date.equals(convertedEndTimeDate);
    }
}
