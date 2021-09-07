package com.woowacourse.zzimkkong.infrastructure.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    private String scope;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("error")
    String error;

    @JsonProperty("error_description")
    String errorDescription;

    @JsonProperty("error_uri")
    String errorUri;

//    @JsonProperty("access_token")
//    private String accessToken;
//    private String expiresIn;
//    private String refreshToken;
//    private String scope;
//    private String tokenType;
//    private String idToken;

    public GoogleTokenResponse(String accessToken, String scope, String tokenType, String error, String errorDescription, String errorUri) {
        this.accessToken = accessToken;
        this.scope = scope;
        this.tokenType = tokenType;
        this.error = error;
        this.errorDescription = errorDescription;
        this.errorUri = errorUri;
    }


//    public GoogleTokenResponse(String accessToken) {
//        this.accessToken = accessToken;
//    }

//    public GoogleTokenResponse(String accessToken, String expiresIn, String refreshToken, String scope, String tokenType, String idToken) {
//        this.accessToken = accessToken;
//        this.expiresIn = expiresIn;
//        this.refreshToken = refreshToken;
//        this.scope = scope;
//        this.tokenType = tokenType;
//        this.idToken = idToken;
//    }
}
