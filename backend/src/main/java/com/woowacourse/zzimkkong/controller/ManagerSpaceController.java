package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
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
            @LoginEmail final LoginEmailDto loginEmailDto) {
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(mapId, spaceCreateRequest, loginEmailDto);
        return ResponseEntity
                .created(URI.create("/api/managers/maps/" + mapId + "/spaces/" + spaceCreateResponse.getId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<SpaceFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @LoginEmail final LoginEmailDto loginEmailDto) {
        SpaceFindAllResponse spaceFindAllResponse = spaceService.findAllSpace(mapId, loginEmailDto);
        return ResponseEntity.ok().body(spaceFindAllResponse);
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceFindDetailResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @LoginEmail final LoginEmailDto loginEmailDto) {
        SpaceFindDetailResponse spaceFindDetailResponse = spaceService.findSpace(mapId, spaceId, loginEmailDto);
        return ResponseEntity.ok().body(spaceFindDetailResponse);
    }

    @PutMapping("/{spaceId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            @LoginEmail final LoginEmailDto loginEmailDto) {
        spaceService.updateSpace(mapId, spaceId, spaceCreateUpdateRequest, loginEmailDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody SpaceDeleteRequest spaceDeleteRequest,
            @LoginEmail final LoginEmailDto loginEmailDto) {
        spaceService.deleteSpace(mapId, spaceId, spaceDeleteRequest, loginEmailDto);
        return ResponseEntity.noContent().build();
    }
}
