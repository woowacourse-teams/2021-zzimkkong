package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.space.SpaceFindAllResponse;
import com.woowacourse.zzimkkong.service.SpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
