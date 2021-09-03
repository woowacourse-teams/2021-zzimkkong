package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class StringToOAuthProviderConverter implements Converter<String, OAuthProvider> {
    @Override
    public OAuthProvider convert(String source) {
        return OAuthProvider.valueOf(source.toUpperCase(Locale.ROOT));
    }
}
