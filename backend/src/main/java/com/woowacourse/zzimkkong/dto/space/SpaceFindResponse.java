package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Space;

public class SpaceFindResponse {
    private Long id;
    private String name;

    private SpaceFindResponse() {
    }

    private SpaceFindResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static SpaceFindResponse from(Space space) {
        return new SpaceFindResponse(space.getId(), space.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
