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
    private String thumbnail;

    @Lob
    private String slackUrl;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_map_member"), nullable = false)
    private Member member;

    @OneToMany(mappedBy = "map", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<Space> spaces = new ArrayList<>();

    public Map(final String name, final String mapDrawing, final String thumbnail, final Member member) {
        this.name = name;
        this.mapDrawing = mapDrawing;
        this.thumbnail = thumbnail;
        this.member = member;

        if (member != null) {
            member.addMap(this);
        }
    }

    public Map(final Long id, final String name, final String mapDrawing, final String thumbnail, final Member member) {
        this(name, mapDrawing, thumbnail, member);
        this.id = id;
    }

    public void update(final String mapName, final String mapDrawing) {
        this.name = mapName;
        this.mapDrawing = mapDrawing;
    }

    public boolean isOwnedBy(final String email) {
        return member.isSameEmail(email);
    }

    public boolean doesNotHaveSpaceId(final Long spaceId) {
        return spaces.stream()
                .noneMatch(space -> space.hasSameId(spaceId));
    }

    public Optional<Space> findSpaceById(final Long spaceId) {
        return spaces.stream()
                .filter(space -> space.hasSameId(spaceId))
                .findFirst();
    }

    public void updateThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void updateSlackUrl(final String slackUrl) {
        this.slackUrl = slackUrl;
    }

    public void addSpace(final Space space) {
        spaces.add(space);
    }

    public List<Space> getSpaces() {
        return Collections.unmodifiableList(spaces);
    }
}
