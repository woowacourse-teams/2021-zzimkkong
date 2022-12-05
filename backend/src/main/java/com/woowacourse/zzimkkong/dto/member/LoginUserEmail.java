package com.woowacourse.zzimkkong.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor
public class LoginUserEmail {
    private static final String NO_LOGIN_EMAIL = "NO_LOGIN_EMAIL";
    public static final LoginUserEmail NO_LOGIN = new LoginUserEmail(NO_LOGIN_EMAIL);

    private String email;

    private LoginUserEmail(final String email) {
        this.email = email;
    }

    public static LoginUserEmail from(final String email) {
        return new LoginUserEmail(email);
    }

    public boolean exists() {
        return StringUtils.isNotBlank(email) && !email.equals(NO_LOGIN_EMAIL);
    }
}
