package com.woowacourse.zzimkkong.dto.space;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SharingSpaceIdResponse {
    private String sharingSpaceId;

    private SharingSpaceIdResponse(final String sharingSpaceId) {
        this.sharingSpaceId = sharingSpaceId;
    }

    public static SharingSpaceIdResponse from(final String sharingSpaceId) {
        return new SharingSpaceIdResponse(sharingSpaceId);
    }
}
