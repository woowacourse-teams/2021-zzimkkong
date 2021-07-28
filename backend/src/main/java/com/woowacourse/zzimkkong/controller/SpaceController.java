package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateResponse;

import com.woowacourse.zzimkkong.dto.space.SpaceFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindResponse;
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
    public ResponseEntity<SpaceFindResponse> find(@PathVariable final Long mapId, @PathVariable final Long spaceId) {
        SpaceFindResponse spaceFindResponse = spaceService.findSpace(mapId, spaceId);
        return ResponseEntity.ok().body(spaceFindResponse);
    }

    @GetMapping
    public ResponseEntity<SpaceFindAllResponse> findAll(@PathVariable final Long mapId, @Manager final Member manager) {
        SpaceFindAllResponse spaceFindAllResponse = spaceService.findAllSpace(mapId, manager);
        return ResponseEntity.ok().body(spaceFindAllResponse);
    }
}
