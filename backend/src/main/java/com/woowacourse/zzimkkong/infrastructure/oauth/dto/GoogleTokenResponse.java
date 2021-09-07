package com.woowacourse.zzimkkong.infrastructure.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("error")
    String error;

    @JsonProperty("error_description")
    String errorDescription;

    public GoogleTokenResponse(String accessToken, String error, String errorDescription) {
        this.accessToken = accessToken;
        this.error = error;
        this.errorDescription = errorDescription;
    }
}
