package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.reservation.CoordinateResponse;

import javax.persistence.*;

@Entity
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 6)
    private String textPosition;

    @Column(nullable = false, length = 24)
    private String color;

    @Column(nullable = false)
    private String coordinate;

    @ManyToOne
    @JoinColumn(name = "map_id", foreignKey = @ForeignKey(name = "fk_space_map"))
    private Map map;

    protected Space() {
    }

    public Space(final String name,
                 final String textPosition,
                 final String color,
                 final String coordinate,
                 final Map map) {
        this.name = name;
        this.textPosition = textPosition;
        this.color = color;
        this.coordinate = coordinate;
        this.map = map;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTextPosition() {
        return textPosition;
    }

    public String getColor() {
        return color;
    }

    public String getCoordinate() {
        return coordinate;
    }
}
