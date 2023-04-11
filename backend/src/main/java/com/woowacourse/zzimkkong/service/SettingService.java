package com.woowacourse.zzimkkong.service;


import com.woowacourse.zzimkkong.domain.EnabledDayOfWeek;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Settings;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.domain.SettingViewType;
import com.woowacourse.zzimkkong.dto.space.SettingsSummaryResponse;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import com.woowacourse.zzimkkong.repository.MapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class SettingService {
    private final MapRepository maps;

    public SettingService(final MapRepository maps) {
        this.maps = maps;
    }

    public SettingsSummaryResponse getSettingsSummary(
            final Long mapId,
            final Long spaceId,
            final LocalDateTime selectedDateTime,
            final String settingViewType) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        String summary = SettingViewType.of(selectedDateTime, settingViewType)
                .getSummary(space, selectedDateTime);

        return SettingsSummaryResponse.from(summary);
    }
}
