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
@RequestMapping("/api/members/maps/{mapId}/spaces")
public class ManagerSpaceController {
    private final SpaceService spaceService;

    public ManagerSpaceController(final SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping
    public ResponseEntity<Void> save(
            @PathVariable final Long mapId,
            @RequestBody @Valid final SpaceCreateUpdateRequest spaceCreateRequest,
            @Manager final Member member) {
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(mapId, spaceCreateRequest, member);
        return ResponseEntity
                .created(URI.create("/api/members/maps/" + mapId + "/spaces/" + spaceCreateResponse.getId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<SpaceFindAllResponse> findAll(@PathVariable final Long mapId, @Manager final Member member) {
        SpaceFindAllResponse spaceFindAllResponse = spaceService.findAllSpace(mapId, member);
        return ResponseEntity.ok().body(spaceFindAllResponse);
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceFindDetailResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @Manager final Member member) {
        SpaceFindDetailResponse spaceFindDetailResponse = spaceService.findSpace(mapId, spaceId, member);
        return ResponseEntity.ok().body(spaceFindDetailResponse);
    }

    @PutMapping("/{spaceId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            @Manager final Member member) {
        spaceService.updateSpace(mapId, spaceId, spaceCreateUpdateRequest, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody SpaceDeleteRequest spaceDeleteRequest,
            @Manager final Member member) {
        spaceService.deleteSpace(mapId, spaceId, spaceDeleteRequest, member);
        return ResponseEntity.noContent().build();
    }
}
