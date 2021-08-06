package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;

@Entity
public class Preset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Setting setting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_preset_member"), nullable = false)
    private Member member;

    protected Preset() {

    }

    public Preset(final Setting setting, final Member member) {
        this.setting = setting;
        this.member = member;
    }

    public Preset(final Long id, final Setting setting, final Member member) {
        this.id = id;
        this.setting = setting;
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Setting getSetting() {
        return setting;
    }
}
