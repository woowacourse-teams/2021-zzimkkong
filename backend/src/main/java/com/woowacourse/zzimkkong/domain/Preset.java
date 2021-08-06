package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;

@Entity
public class Preset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long managerId;

    @Embedded
    private Setting setting;

    protected Preset() {

    }

    public Preset(final Long managerId, final Setting setting) {
        this.managerId = managerId;
        this.setting = setting;
    }

    public Preset(final Long id, final Long managerId, final Setting setting) {
        this.id = id;
        this.managerId = managerId;
        this.setting = setting;
    }

    public Long getId() {
        return id;
    }

    public Long getManagerId() {
        return managerId;
    }

    public Setting getSetting() {
        return setting;
    }
}
