package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.space.SpaceFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailResponse;
import com.woowacourse.zzimkkong.service.SpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/guests/maps/{mapId}/spaces")
public class GuestSpaceController {
    private final SpaceService spaceService;

    public GuestSpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
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
