package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Preset;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PresetFindAllResponse {
    private List<PresetFindResponse> presets;

    private PresetFindAllResponse(final List<PresetFindResponse> presets) {
        this.presets = presets;
    }

    public static PresetFindAllResponse from(final List<Preset> findPresets) {
        List<PresetFindResponse> presetFindResponses = findPresets.stream()
                .map(PresetFindResponse::from)
                .collect(Collectors.toList());
        return new PresetFindAllResponse(presetFindResponses);
    }
}
