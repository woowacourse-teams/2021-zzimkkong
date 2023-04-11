package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.dto.space.SettingsSummaryResponse;
import com.woowacourse.zzimkkong.domain.SettingViewType;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import com.woowacourse.zzimkkong.service.SettingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATETIME_FORMAT;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/maps/{mapId}/spaces/{spaceId}/settings")
public class SettingController {
    private final SettingService settingService;

    public SettingController(final SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/summary")
    public ResponseEntity<SettingsSummaryResponse> getSettingsSummary(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATETIME_FORMAT) final ZonedDateTime selectedDateTime,
            @RequestParam(required = false, defaultValue = "FLAT") final String settingViewType) {
        SettingsSummaryResponse settingsSummaryResponse = settingService.getSettingsSummary(
                mapId,
                spaceId,
                TimeZoneUtils.convertToUTC(selectedDateTime),
                settingViewType);
        return ResponseEntity.ok().body(settingsSummaryResponse);
    }
}
