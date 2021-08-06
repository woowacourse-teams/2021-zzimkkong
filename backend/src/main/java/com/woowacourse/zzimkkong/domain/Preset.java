package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;

@Entity
public class Preset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Setting setting;

    @ManyToOne
    @JoinColumn(name = "manager_id", foreignKey = @ForeignKey(name = "fk_preset_member"), nullable = false)
    private Member manager;

    protected Preset() {

    }

    public Preset(final Setting setting, final Member manager) {
        this.setting = setting;
        this.manager = manager;
    }

    public Preset(final Long id, final Setting setting, final Member manager) {
        this.id = id;
        this.setting = setting;
        this.manager = manager;
    }

    public Long getId() {
        return id;
    }

    public Member getManager() {
        return manager;
    }

    public Setting getSetting() {
        return setting;
    }
}
