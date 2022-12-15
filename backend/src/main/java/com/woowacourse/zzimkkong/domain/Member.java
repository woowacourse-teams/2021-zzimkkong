package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.member.MemberUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = @Index(name = "email", columnList = "email", unique = true))
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 20, unique = true)
    private String userName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProfileEmoji emoji = ProfileEmoji.MAN_LIGHT_SKIN_TONE_TECHNOLOGIST;

    @Column(length = 128)
    private String password;

    @Column(nullable = false, length = 20)
    private String organization;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private final List<Preset> presets = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private final List<Map> maps = new ArrayList<>();

    public Optional<Preset> findPresetById(final Long presetId) {
        return this.presets.stream()
                .filter(preset -> preset.hasSameId(presetId))
                .findAny();
    }

    public void addMap(final Map map) {
        this.maps.add(map);
    }

    public void addPreset(final Preset preset) {
        this.presets.add(preset);
    }

    public List<Preset> getPresets() {
        return Collections.unmodifiableList(presets);
    }

    public void update(final MemberUpdateRequest memberUpdateRequest) {
        this.organization = memberUpdateRequest.getOrganization();
        this.userName = memberUpdateRequest.getUserName();
        this.emoji = memberUpdateRequest.getEmoji();
    }

    public boolean hasEmail(String email) {
        return this.email.equals(email);
    }
}
