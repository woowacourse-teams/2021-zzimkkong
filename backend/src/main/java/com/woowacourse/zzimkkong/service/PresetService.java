package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.PresetCreateRequest;
import com.woowacourse.zzimkkong.dto.member.PresetCreateResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.repository.PresetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PresetService {
    private final PresetRepository presets;

    public PresetService(final PresetRepository presets) {
        this.presets = presets;
    }

    public PresetCreateResponse savePreset(final PresetCreateRequest presetCreateRequest, final Member manager) {
        SettingsRequest settingsRequest = presetCreateRequest.getSettingsRequest();
        Setting setting = new Setting.Builder()
                .availableStartTime(settingsRequest.getAvailableStartTime())
                .availableEndTime(settingsRequest.getAvailableEndTime())
                .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                .reservationEnable(settingsRequest.getReservationEnable())
                .enabledDayOfWeek(settingsRequest.getEnabledDayOfWeek())
                .build();

        Preset preset = presets.save(new Preset(presetCreateRequest.getName(), setting, manager));

        return PresetCreateResponse.from(preset);
    }

    public void delete(Long presetId) {
        Preset preset = presets.findById(presetId).orElseThrow(IllegalArgumentException::new);
        presets.delete(preset);
    }
}
