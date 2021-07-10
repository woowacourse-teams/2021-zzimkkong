package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    protected Reservation(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String password,
            String userName,
            String description,
            Space space) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.password = password;
        this.userName = userName;
        this.description = description;
        this.space = space;
    }

    public static Reservation from(ReservationSaveRequest reservationSaveRequest, Space space) {
        return new Reservation(
                reservationSaveRequest.getStartDateTime(),
                reservationSaveRequest.getEndDateTime(),
                reservationSaveRequest.getPassword(),
                reservationSaveRequest.getName(),
                reservationSaveRequest.getDescription(),
                space);
    }


    public Long getId() {
        return id;
    }
}
