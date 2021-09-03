package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringToOAuthProviderConverterTest {
    @Test
    @DisplayName("OAuth 제공사 이름 문자열로부터 enum 객체를 찾을 수 있다.")
    void convert() {
        // given
        String oAuthProvider = "Github";

        // when
        StringToOAuthProviderConverter stringToOAuthProviderConverter = new StringToOAuthProviderConverter();
        OAuthProvider actual = stringToOAuthProviderConverter.convert(oAuthProvider);

        // then
        assertThat(actual).isSameAs(OAuthProvider.GITHUB);
    }
}
