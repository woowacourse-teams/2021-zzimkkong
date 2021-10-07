package com.woowacourse.zzimkkong.infrastructure.sharingid;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.exception.infrastructure.DecodingException;
import com.woowacourse.zzimkkong.exception.map.InvalidAccessLinkException;
import org.springframework.stereotype.Component;

@Component
@LogMethodExecutionTime(group = "infrastructure")
public class SharingIdGenerator {
    private final Transcoder transcoder;

    public SharingIdGenerator(final Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    public String from(final Map map) {
        return transcoder.encode(map.getId().toString());
    }

    public Long parseIdFrom(final String publicId) {
        try {
            String decoded = transcoder.decode(publicId);
            return Long.parseLong(decoded);
        } catch (DecodingException | NumberFormatException exception) {
            throw new InvalidAccessLinkException();
        }
    }
}
