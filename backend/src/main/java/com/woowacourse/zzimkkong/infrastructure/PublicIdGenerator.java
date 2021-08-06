package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.exception.infrastructure.DecodingException;
import com.woowacourse.zzimkkong.exception.map.InvalidAccessLinkException;
import org.springframework.stereotype.Component;

@Component
public class PublicIdGenerator {
    private final Transcoder transcoder;

    public PublicIdGenerator(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    public String from(Map map) {
        return transcoder.encode(map.getId().toString());
    }

    public Long parseIdFrom(String publicId) {
        try {
            String decoded = transcoder.decode(publicId);
            return Long.parseLong(decoded);
        } catch (DecodingException | NumberFormatException exception) {
            throw new InvalidAccessLinkException();
        }
    }
}
