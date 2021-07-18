package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.space.SpaceFindResponse;
import com.woowacourse.zzimkkong.service.SpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maps/{mapId}/spaces")
public class SpaceController {
    private final SpaceService spaceService;

    public SpaceController(final SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceFindResponse> find(@PathVariable Long mapId, @PathVariable Long spaceId) {
        SpaceFindResponse spaceFindResponse = spaceService.findSpace(mapId, spaceId);
        return ResponseEntity.ok().body(spaceFindResponse);
    }
}
