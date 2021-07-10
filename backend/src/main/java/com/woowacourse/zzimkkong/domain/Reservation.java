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
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.password = builder.password;
        this.userName = builder.userName;
        this.description = builder.description;
        this.space = builder.space;
    }

    public static class Builder {
        private LocalDateTime startTime = null;
        private LocalDateTime endTime = null;
        private String password = null;
        private String userName = null;
        private String description = null;
        private Space space = null;

        public Builder() {
        }

        public Builder startTime(LocalDateTime inputStartTime) {
            startTime = inputStartTime;
            return this;
        }

        public Builder endTime(LocalDateTime inputEndTime) {
            endTime = inputEndTime;
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

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
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
