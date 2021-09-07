package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import com.woowacourse.zzimkkong.infrastructure.oauth.dto.GoogleTokenResponse;
import com.woowacourse.zzimkkong.infrastructure.oauth.dto.GoogleUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
@PropertySource("classpath:config/oauth.properties")
public class GoogleRequester implements OAuthAPIRequester {
    private final String clientId;
    private final String secretId;
    private final String redirectUri;

    private GoogleRequester(
            @Value("${google.client-id}")
                    String clientId,
            @Value("${google.secret-id}")
                    String secretId,
            @Value("${google.redirect-uri}")
            String redirectUri) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.redirectUri = redirectUri;
    }

    @Override
    public boolean supports(OAuthProvider oAuthProvider) {
        return OAuthProvider.GOOGLE.equals(oAuthProvider);
    }

    @Override
    public OAuthUserInfo getUserInfoByCode(String code) {
        String token = getToken(code);
        return getUserInfo(token);
    }

    private String getToken(String code) {
        OAuthProvider oauthProvider = OAuthProvider.GOOGLE;
        GoogleTokenResponse googleTokenResponse = WebClient.create()
                .post()
                .uri("https://www.googleapis.com/oauth2/v4/token")
                .headers(header -> {
                    header.setBasicAuth(clientId, secretId);
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code))
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();
//        Mono<ResponseEntity<GoogleTokenResponse>> googleTokenResponse = WebClient.create()
//                .post()
//                .uri(uri -> uri.path(oauthProvider.getTokenUri())
//                        .queryParam("code", code)
//                        .queryParam("client_id", clientId)
//                        .queryParam("client_secret", secretId)
//                        .queryParam("redirect_uri", redirectUri)
//                        .queryParam("grant_type", "authorization_code")
//                        .build())
//                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new RuntimeException(clientResponse.toString())))
//                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new RuntimeException(clientResponse.toString())))
//                .toEntity(GoogleTokenResponse.class);
//        ResponseEntity<GoogleTokenResponse> s = googleTokenResponse.block();
//        GoogleTokenResponse body = s.getBody();
//        String accessToken = body.getAccessToken();

        return googleTokenResponse.getAccessToken();
    }

    private MultiValueMap<String, String> tokenRequest(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", redirectUri);
        return formData;
    }

    private OAuthUserInfo getUserInfo(String token) {
        //todo 샐리
        return null;
        //"https://www.googleapis.com/oauth2/v2/userinfo"

//        return WebClient.create()
//                .get()
//                .uri(OAuthProvider.GOOGLE.getUserApi())
//                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
//                .retrieve()
//                .bodyToMono(GoogleUserResponse.class)
//                .block();
    }
}
