package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;

@Entity
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToOne
    @JoinColumn(name = "map_id", foreignKey = @ForeignKey(name = "fk_space_map"))
    private Map map;

    protected Space() {
    }

    public Space(final String name, final Map map) {
        this.name = name;
        this.map = map;
    }
}
