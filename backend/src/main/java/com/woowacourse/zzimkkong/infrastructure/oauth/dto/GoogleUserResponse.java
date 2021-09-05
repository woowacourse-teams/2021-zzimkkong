package com.woowacourse.zzimkkong.infrastructure.oauth.dto;

import com.woowacourse.zzimkkong.infrastructure.oauth.OAuthUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserResponse implements OAuthUserInfo {
    private String id;
    private String email;
    private String verifiedEmail;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String locale;

    public GoogleUserResponse(String id, String email, String verifiedEmail, String name, String givenName, String familyName, String picture, String locale) {
        this.id = id;
        this.email = email;
        this.verifiedEmail = verifiedEmail;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
        this.locale = locale;
    }
}
