package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Preset;

public class PresetCreateResponse {
    private Long id;

    public PresetCreateResponse() {
    }

    private PresetCreateResponse(final Long id) {
        this.id = id;
    }

    public static PresetCreateResponse from(final Preset preset) {
        return new PresetCreateResponse(preset.getId());
    }

    public Long getId() {
        return id;
    }
}
