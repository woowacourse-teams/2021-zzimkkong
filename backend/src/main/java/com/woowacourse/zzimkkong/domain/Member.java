package com.woowacourse.zzimkkong.domain;

import javax.persistence.*;

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

    protected Member() {
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

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getOrganization() {
        return organization;
    }
}
