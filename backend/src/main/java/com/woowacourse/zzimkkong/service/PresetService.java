package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.member.PresetCreateResponse;
import com.woowacourse.zzimkkong.dto.member.PresetFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.member.PresetCreateRequest;
import com.woowacourse.zzimkkong.exception.preset.NoSuchPresetException;
import com.woowacourse.zzimkkong.repository.PresetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PresetService {
    private final PresetRepository presets;

    public PresetService(final PresetRepository presets) {
        this.presets = presets;
    }

    public PresetCreateResponse savePreset(final PresetCreateRequest presetCreateRequest, final Member manager) {
        SettingsRequest settingsRequest = presetCreateRequest.getSettingsRequest();
        Setting setting = Setting.builder()
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

    @Transactional(readOnly = true)
    public PresetFindAllResponse findAllPresets(final Member manager) {
        List<Preset> findPresets = manager.getPresets();
        return PresetFindAllResponse.from(findPresets);
    }

    public void deletePreset(final Long presetId, final Member manager) {
        Preset preset = manager.findPresetById(presetId)
                .orElseThrow(NoSuchPresetException::new);

        presets.delete(preset);
    }
}
