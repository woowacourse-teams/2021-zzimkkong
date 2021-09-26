package com.woowacourse.zzimkkong.infrastructure;

public class LoginEmail {
    private String email;

    public LoginEmail() {
    }

    private LoginEmail(String email) {
        this.email = email;
    }

    public static LoginEmail from(String email) {
        return new LoginEmail(email);
    }

    public String getEmail() {
        return email;
    }
}
