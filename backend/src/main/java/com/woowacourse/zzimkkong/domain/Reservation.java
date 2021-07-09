package com.woowacourse.zzimkkong.domain;

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

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false, length = 20)
    private String userName;

    @Column(nullable = false, length = 20)
    private String password;

    @ManyToOne
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "fk_reservation_space"))
    private Space space;

    protected Reservation() {
    }

    public Reservation(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String description,
            String userName,
            String password,
            Space space) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.userName = userName;
        this.password = password;
        this.space = space;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}
