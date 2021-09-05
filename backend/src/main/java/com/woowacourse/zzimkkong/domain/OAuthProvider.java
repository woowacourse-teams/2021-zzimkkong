package com.woowacourse.zzimkkong.domain;

public enum OAuthProvider {
    GITHUB("github.com", "github"),
    GOOGLE("https://www.googleapis.com/oauth2/v4/token", "https://www.googleapis.com/oauth2/v2/userinfo");

    private String tokenUri;
    private String userApi;

    OAuthProvider(String tokenUri, String userApi) {
        this.tokenUri = tokenUri;
        this.userApi = userApi;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserApi() {
        return userApi;
    }
}
