package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;

@Entity
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_map_member"))
    private Member member;

    protected Map() {
    }

    public Map(final String name, final Member member) {
        this.name = name;
        this.member = member;
    }

    public Map(final Long id, final String name, final Member member) {
        this.id = id;
        this.name = name;
        this.member = member;
    }

    public boolean hasSameId(final Long id) {
        return this.id.equals(id);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public boolean isNotOwnedBy(final Member manager) {
        return !this.member.equals(manager);
    }
}
