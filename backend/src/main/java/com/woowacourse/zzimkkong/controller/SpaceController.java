package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;

import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.service.SpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/managers/maps/{mapId}/spaces")
public class SpaceController {
    private final SpaceService spaceService;

    public SpaceController(final SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping
    public ResponseEntity<Void> save(
            @PathVariable final Long mapId,
            @RequestBody @Valid final SpaceCreateRequest spaceCreateRequest,
            @Manager final Member manager) {
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(mapId, spaceCreateRequest, manager);
        return ResponseEntity
                .created(URI.create("/api/managers/maps/" + mapId + "/spaces/" + spaceCreateResponse.getId()))
                .build();
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceFindDetailResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @Manager Member manager) {
        SpaceFindDetailResponse spaceFindDetailResponse = spaceService.findSpace(mapId, spaceId, manager);
        return ResponseEntity.ok().body(spaceFindDetailResponse);
    }

    @GetMapping
    public ResponseEntity<SpaceFindAllResponse> findAll(@PathVariable Long mapId, @Manager Member manager) {
        SpaceFindAllResponse spaceFindAllResponse = spaceService.findAllSpace(mapId, manager);
        return ResponseEntity.ok().body(spaceFindAllResponse);
    }
}
