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

}
