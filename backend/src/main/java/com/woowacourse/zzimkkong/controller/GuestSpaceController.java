package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.dto.space.SpaceFindAllAvailabilityResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailResponse;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import com.woowacourse.zzimkkong.service.SpaceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATETIME_FORMAT;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/guests/maps/{mapId}/spaces")
public class GuestSpaceController {
    private final SpaceService spaceService;

    public GuestSpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @GetMapping("/availability")
    public ResponseEntity<SpaceFindAllAvailabilityResponse> findAllSpaceAvailability(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) final ZonedDateTime startDateTime,
            @RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) final ZonedDateTime endDateTime) {
        SpaceFindAllAvailabilityResponse spaceFindAllAvailabilityResponse = spaceService.findAllSpaceAvailability(
                mapId,
                TimeZoneUtils.convertToUTC(startDateTime),
                TimeZoneUtils.convertToUTC(endDateTime));
        return ResponseEntity.ok().body(spaceFindAllAvailabilityResponse);
    }

    @GetMapping
    public ResponseEntity<SpaceFindAllResponse> findAll(@PathVariable final Long mapId) {
        SpaceFindAllResponse spaceFindAllResponse = spaceService.findAllSpace(mapId);
        return ResponseEntity.ok().body(spaceFindAllResponse);
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceFindDetailResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId) {
        SpaceFindDetailResponse spaceFindDetailResponse = spaceService.findSpace(mapId, spaceId);
        return ResponseEntity.ok().body(spaceFindDetailResponse);
    }
}
