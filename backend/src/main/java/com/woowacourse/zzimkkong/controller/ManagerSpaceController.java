package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.service.SpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@LogMethodExecutionTime(group = "controller")
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
            @com.woowacourse.zzimkkong.domain.LoginEmail final LoginUserEmail loginUserEmail) {
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(mapId, spaceCreateRequest, loginUserEmail);
        return ResponseEntity
                .created(URI.create("/api/managers/maps/" + mapId + "/spaces/" + spaceCreateResponse.getId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<SpaceFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @com.woowacourse.zzimkkong.domain.LoginEmail final LoginUserEmail loginUserEmail) {
        SpaceFindAllResponse spaceFindAllResponse = spaceService.findAllSpace(mapId, loginUserEmail);
        return ResponseEntity.ok().body(spaceFindAllResponse);
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceFindDetailResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @com.woowacourse.zzimkkong.domain.LoginEmail final LoginUserEmail loginUserEmail) {
        SpaceFindDetailResponse spaceFindDetailResponse = spaceService.findSpace(mapId, spaceId, loginUserEmail);
        return ResponseEntity.ok().body(spaceFindDetailResponse);
    }

    @PutMapping("/{spaceId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            @com.woowacourse.zzimkkong.domain.LoginEmail final LoginUserEmail loginUserEmail) {
        spaceService.updateSpace(mapId, spaceId, spaceCreateUpdateRequest, loginUserEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody SpaceDeleteRequest spaceDeleteRequest,
            @com.woowacourse.zzimkkong.domain.LoginEmail final LoginUserEmail loginUserEmail) {
        spaceService.deleteSpace(mapId, spaceId, spaceDeleteRequest, loginUserEmail);
        return ResponseEntity.noContent().build();
    }
}
