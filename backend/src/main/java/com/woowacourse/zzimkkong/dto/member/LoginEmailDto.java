package com.woowacourse.zzimkkong.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginEmailDto {
    private String email;

    private LoginEmailDto(String email) {
        this.email = email;
    }

    public static LoginEmailDto from(String email) {
        return new LoginEmailDto(email);
    }
}
