package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.infrastructure.oauth.StringToOauthProviderConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringToOauthProviderConverterTest {
    @Test
    @DisplayName("Oauth 제공사 이름 문자열로부터 enum 객체를 찾을 수 있다.")
    void convert() {
        // given
        String oauthProvider = "Github";

        // when
        StringToOauthProviderConverter stringToOauthProviderConverter = new StringToOauthProviderConverter();
        OauthProvider actual = stringToOauthProviderConverter.convert(oauthProvider);

        // then
        assertThat(actual).isSameAs(OauthProvider.GITHUB);
    }
}
