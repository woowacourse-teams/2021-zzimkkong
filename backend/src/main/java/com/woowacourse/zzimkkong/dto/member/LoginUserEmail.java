package com.woowacourse.zzimkkong.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginUserEmail {
    private String email;

    private LoginUserEmail(String email) {
        this.email = email;
    }

    public static LoginUserEmail from(String email) {
        return new LoginUserEmail(email);
    }
}
