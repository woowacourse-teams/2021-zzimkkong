package com.woowacourse.zzimkkong.domain.oauth;

import com.woowacourse.zzimkkong.exception.infrastructure.oauth.NoPublicEmailHasBeenSetOnGithubException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GithubUserInfoTest {
    @Test
    @DisplayName("map으로 받아온 정보로부터 email을 가져온다.")
    void getEmail() {
        //given
        String email = "email@email.com";
        Map<String, Object> info = Map.of("email", email);

        //when
        OauthUserInfo githubUserInfo = GithubUserInfo.from(info);

        //then
        assertThat(githubUserInfo.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("map으로 받아온 정보에 email이 존재하지 않으면 오류가 발생한다.")
    void getEmailException() {
        //given
        Map<String, Object> info = Map.of("name", "name");

        //when
        OauthUserInfo githubUserInfo = GithubUserInfo.from(info);

        //then
        assertThatThrownBy(githubUserInfo::getEmail)
                .isInstanceOf(NoPublicEmailHasBeenSetOnGithubException.class);
    }
}
