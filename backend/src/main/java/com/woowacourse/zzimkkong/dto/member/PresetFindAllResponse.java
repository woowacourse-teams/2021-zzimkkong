package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.dto.space.SettingResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PresetFindAllResponse {
    private List<SettingResponse> presets;

    public PresetFindAllResponse() {
    }

    private PresetFindAllResponse(final List<SettingResponse> presets) {
        this.presets = presets;
    }

    public static PresetFindAllResponse from(final List<Preset> findPresets) {
        List<SettingResponse> presetFindResponses = findPresets.stream()
                .map(SettingResponse::from)
                .collect(Collectors.toList());
        return new PresetFindAllResponse(presetFindResponses);
    }

    public List<SettingResponse> getPresets() {
        return presets;
    }
}
