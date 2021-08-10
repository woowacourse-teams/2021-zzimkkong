package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Preset;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresetCreateResponse {
    private Long id;

    private PresetCreateResponse(final Long id) {
        this.id = id;
    }

    public static PresetCreateResponse from(final Preset preset) {
        return new PresetCreateResponse(preset.getId());
    }
}
