package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.member.MemberUpdateRequest;
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
@Table(indexes = @Index(name = "email", columnList = "email", unique = true))
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 20, unique = true)
    private String userName;

    @Column(length = 128)
    private String password;

    @Column(nullable = false, length = 20)
    private String organization;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Preset> presets = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Map> maps = new ArrayList<>();

    public Member(
            final String email,
            final String userName,
            final String password,
            final String organization) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.organization = organization;
    }

    public Member(
            final Long id,
            final String email,
            final String userName,
            final String password,
            final String organization) {
        this(email, password, organization, userName);
        this.id = id;
    }

    public Member(final String email, final String userName, final String organization, final OauthProvider oauthProvider) {
        this.email = email;
        this.userName = userName;
        this.organization = organization;
        this.oauthProvider = oauthProvider;
    }

    public Member(final Long id, final String email, final String userName, final String organization, final OauthProvider oauthProvider) {
        this(email, userName, organization, oauthProvider);
        this.id = id;
    }

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
    }

    public boolean hasEmail(String email) {
        return this.email.equals(email);
    }
}
