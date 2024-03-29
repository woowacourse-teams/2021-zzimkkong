package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.*;

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

    @Lob
    private String notice;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_map_member"), nullable = false)
    private Member member;

    @OneToMany(mappedBy = "map", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Space> spaces = new ArrayList<>();

    /**
     * The default Zzimkkong service zone (Asia/Seoul)
     * 추후 timezone 관련 확장성을 고려한다면 Map Table에 Column으로 추가 될 수도 있음
     * 일단은 transient field 로 구현
     */
    @Transient
    private final ServiceZone serviceZone = ServiceZone.KOREA;

    @Transient
    private String sharingMapId;

    public Map(final String name,
               final String mapDrawing,
               final String thumbnail,
               final Member member) {
        this.name = name;
        this.mapDrawing = mapDrawing;
        this.thumbnail = thumbnail;
        this.member = member;

        if (member != null) {
            member.addMap(this);
        }
    }

    public Map(final Long id,
               final String name,
               final String mapDrawing,
               final String thumbnail,
               final Member member) {
        this(name, mapDrawing, thumbnail, member);
        this.id = id;
    }

    public void update(final String mapName, final String mapDrawing) {
        this.name = mapName;
        this.mapDrawing = mapDrawing;
    }

    public boolean isOwnedBy(final String email) {
        return member.hasEmail(email);
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

    public void updateNotice(final String notice) {
        this.notice = notice;
    }

    public void addSpace(final Space space) {
        spaces.add(space);
    }

    public void activateSharingMapId(final SharingIdGenerator sharingIdGenerator) {
        if (StringUtils.isNotBlank(this.sharingMapId)) {
            return;
        }
        this.sharingMapId = sharingIdGenerator.from(this);
    }

    public List<Space> getSpaces() {
        return Collections.unmodifiableList(spaces);
    }
}
