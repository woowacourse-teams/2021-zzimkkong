package com.woowacourse.zzimkkong.infrastructure.sharingid;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.exception.infrastructure.DecodingException;
import com.woowacourse.zzimkkong.exception.map.InvalidAccessLinkException;
import org.springframework.stereotype.Component;

@Component
@LogMethodExecutionTime(group = "infrastructure")
public class SharingIdGenerator {
    private static final String SPACE_PREFIX = "space:";

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

    public String fromSpace(final Space space) {
        return transcoder.encode(SPACE_PREFIX + space.getId());
    }

    public Long parseSpaceIdFrom(final String sharingSpaceId) {
        try {
            String decoded = transcoder.decode(sharingSpaceId);
            if (!decoded.startsWith(SPACE_PREFIX)) {
                throw new InvalidAccessLinkException();
            }
            return Long.parseLong(decoded.substring(SPACE_PREFIX.length()));
        } catch (DecodingException | NumberFormatException exception) {
            throw new InvalidAccessLinkException();
        }
    }
}
