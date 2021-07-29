package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
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
            @RequestBody @Valid final SpaceCreateUpdateRequest spaceCreateRequest,
            @Manager final Member manager) {
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(mapId, spaceCreateRequest, manager);
        return ResponseEntity
                .created(URI.create("/api/managers/maps/" + mapId + "/spaces/" + spaceCreateResponse.getId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<SpaceFindAllResponse> findAll(@PathVariable final Long mapId, @Manager final Member manager) {
        SpaceFindAllResponse spaceFindAllResponse = spaceService.findAllSpace(mapId, manager);
        return ResponseEntity.ok().body(spaceFindAllResponse);
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceFindDetailResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @Manager final Member manager) {
        SpaceFindDetailResponse spaceFindDetailResponse = spaceService.findSpace(mapId, spaceId, manager);
        return ResponseEntity.ok().body(spaceFindDetailResponse);
    }

    @PutMapping("/{spaceId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            @Manager final Member manager) {
        spaceService.updateSpace(mapId, spaceId, spaceCreateUpdateRequest, manager);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @Manager final Member manager) {
        spaceService.deleteSpace(mapId, spaceId, manager);
        return ResponseEntity.noContent().build();
    }
}
