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
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(length = 128)
    private String password;

    @Column(nullable = false, length = 20)
    private String organization;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Preset> presets = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Map> maps = new ArrayList<>();

    public Member(
            final String email,
            final String password,
            final String organization) {
        this.email = email;
        this.password = password;
        this.organization = organization;
    }

    public Member(
            final Long id,
            final String email,
            final String password,
            final String organization) {
        this(email, password, organization);
        this.id = id;
    }

    public Member(final String email, final String organization, final OauthProvider oauthProvider) {
        this.email = email;
        this.organization = organization;
        this.oauthProvider = oauthProvider;
    }

    public Member(final Long id, final String email, final String organization, final OauthProvider oauthProvider) {
        this(email, organization, oauthProvider);
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

    public void update(final String organization) {
        this.organization = organization;
    }

    public boolean isSameEmail(String email) {
        return this.email.equals(email);
    }
}
