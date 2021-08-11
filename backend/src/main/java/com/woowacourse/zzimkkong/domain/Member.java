package com.woowacourse.zzimkkong.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 20)
    private String organization;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Preset> presets = new ArrayList<>();

    public Member() {
    }

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

    public boolean checkPassword(final String password) {
        return this.password.equals(password);
    }

    public Optional<Preset> findPresetById(final Long presetId) {
        return this.presets.stream()
                .filter(preset -> preset.hasSameId(presetId))
                .findAny();
    }

    public void addPreset(final Preset preset) {
        this.presets.add(preset);
    }

    public List<Preset> getPresets() {
        return Collections.unmodifiableList(presets);
    }
}
