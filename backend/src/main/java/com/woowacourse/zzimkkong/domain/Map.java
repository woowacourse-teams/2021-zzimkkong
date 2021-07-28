package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;

@Entity
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private String mapDrawing;

    @Column(nullable = false)
    private String mapImage;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_map_member"))
    private Member member;

    protected Map() {
    }

    public Map(Long id, String name, String mapDrawing, String mapImage, Member member) {
        this(name, mapDrawing, mapImage, member);
        this.id = id;
    }

    public Map(String name, String mapDrawing, String mapImage, Member member) {
        this.name = name;
        this.mapDrawing = mapDrawing;
        this.mapImage = mapImage;
        this.member = member;
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

    public String getName() {
        return name;
    }

    public String getMapDrawing() {
        return mapDrawing;
    }

    public String getMapImage() {
        return mapImage;
    }
}
