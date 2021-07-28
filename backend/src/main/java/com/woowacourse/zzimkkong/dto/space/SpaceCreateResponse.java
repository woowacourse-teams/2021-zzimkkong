package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Space;

public class SpaceCreateResponse {
    private Long id;

    public SpaceCreateResponse() {
    }

    private SpaceCreateResponse(final Long id) {
        this.id = id;
    }

    public static SpaceCreateResponse from(final Space space) {
        return new SpaceCreateResponse(space.getId());
    }

    public Long getId() {
        return id;
    }
}
