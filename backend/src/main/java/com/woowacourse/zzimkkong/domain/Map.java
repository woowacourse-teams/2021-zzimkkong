package com.woowacourse.zzimkkong.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
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

    @OneToMany(mappedBy = "map", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Space> spaces = new ArrayList<>();

    public Map(final String name, final String mapDrawing, final String mapImageUrl, final Member member) {
        this.name = name;
        this.mapDrawing = mapDrawing;
        this.mapImageUrl = mapImageUrl;
        this.member = member;

        if (member != null) {
            member.addMap(this);
        }
    }

    public Map(final Long id, final String name, final String mapDrawing, final String mapImageUrl, final Member member) {
        this(name, mapDrawing, mapImageUrl, member);
        this.id = id;
    }

    public void update(final String mapName, final String mapDrawing) {
        this.name = mapName;
        this.mapDrawing = mapDrawing;
    }

    public boolean isNotOwnedBy(final Member manager) {
        return !this.member.equals(manager);
    }

    public Boolean doesNotHaveSpaceId(final Long spaceId) {
        return spaces.stream()
                .noneMatch(space -> space.hasSameId(spaceId));
    }

    public Optional<Space> findSpaceById(final Long spaceId) {
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

    public List<Space> getSpaces() {
        return Collections.unmodifiableList(spaces);
    }
}
