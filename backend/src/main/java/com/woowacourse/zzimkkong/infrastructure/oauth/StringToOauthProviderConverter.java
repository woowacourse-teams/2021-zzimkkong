package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class StringToOauthProviderConverter implements Converter<String, OauthProvider> {
    @Override
    public OauthProvider convert(String source) {
        return OauthProvider.valueOf(source.toUpperCase(Locale.ROOT));
    }
}
