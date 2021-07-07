package com.woowacourse.zzimkkong.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToOne
    @JoinColumn( name = "map_id", foreignKey = @ForeignKey(name = "fk_space_map"))
    private Map map;

    protected Space() {
    }

    public Space(String name, Map map) {
        this.name = name;
        this.map = map;
    }
}
