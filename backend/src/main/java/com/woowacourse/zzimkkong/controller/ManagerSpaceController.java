package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
import com.woowacourse.zzimkkong.service.SpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/managers/maps/{mapId}/spaces")
public class ManagerSpaceController {
    private final SpaceService spaceService;

    public ManagerSpaceController(final SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping
    public ResponseEntity<Void> save(
            @PathVariable final Long mapId,
            @RequestBody @Valid final SpaceCreateUpdateRequest spaceCreateRequest,
            @Manager final LoginEmail loginEmail) {
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(mapId, spaceCreateRequest, loginEmail);
        return ResponseEntity
                .created(URI.create("/api/managers/maps/" + mapId + "/spaces/" + spaceCreateResponse.getId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<SpaceFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @Manager final LoginEmail loginEmail) {
        SpaceFindAllResponse spaceFindAllResponse = spaceService.findAllSpace(mapId, loginEmail);
        return ResponseEntity.ok().body(spaceFindAllResponse);
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceFindDetailResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @Manager final LoginEmail loginEmail) {
        SpaceFindDetailResponse spaceFindDetailResponse = spaceService.findSpace(mapId, spaceId, loginEmail);
        return ResponseEntity.ok().body(spaceFindDetailResponse);
    }

    @PutMapping("/{spaceId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            @Manager final LoginEmail loginEmail) {
        spaceService.updateSpace(mapId, spaceId, spaceCreateUpdateRequest, loginEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody SpaceDeleteRequest spaceDeleteRequest,
            @Manager final LoginEmail loginEmail) {
        spaceService.deleteSpace(mapId, spaceId, spaceDeleteRequest, loginEmail);
        return ResponseEntity.noContent().build();
    }
}
