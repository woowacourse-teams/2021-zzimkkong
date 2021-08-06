package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    @Lob
    private String mapDrawing;

    @Column(nullable = false)
    @Lob
    private String mapImageUrl;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_map_member"), nullable = false)
    private Member member;

    @OneToMany(mappedBy = "map", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Space> spaces = new ArrayList<>();

    protected Map() {
    }

    public Map(String name, String mapDrawing, String mapImageUrl, Member member) {
        this.name = name;
        this.mapDrawing = mapDrawing;
        this.mapImageUrl = mapImageUrl;
        this.member = member;
    }

    public Map(Long id, String name, String mapDrawing, String mapImageUrl, Member member) {
        this(name, mapDrawing, mapImageUrl, member);
        this.id = id;
    }

    public void update(String mapName, String mapDrawing, String mapImageUrl) {
        this.name = mapName;
        this.mapDrawing = mapDrawing;
        this.mapImageUrl = mapImageUrl;
    }

    public boolean isNotOwnedBy(final Member manager) {
        return !this.member.equals(manager);
    }

    public Boolean doesNotHaveSpaceId(final Long spaceId) {
        return spaces.stream()
                .noneMatch(space -> space.hasSameId(spaceId));
    }

    public Optional<Space> getSpaceById(final Long spaceId) {
        return spaces.stream()
                .filter(space -> space.hasSameId(spaceId))
                .findFirst();
    }

    public void updateImageUrl(final String mapImageUrl) {
        this.mapImageUrl = mapImageUrl;
    }

    public void addSpace(final Space space) {
        spaces.add(space);
    }

    public void addAllSpaces(final List<Space> spaces) {
        this.spaces.addAll(spaces);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getName() {
        return name;
    }

    public String getMapDrawing() {
        return mapDrawing;
    }

    public String getMapImageUrl() {
        return mapImageUrl;
    }

    public List<Space> getSpaces() {
        return spaces;
    }
}
