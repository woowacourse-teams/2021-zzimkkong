package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
import com.woowacourse.zzimkkong.service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/managers/maps")
public class MapController {
    private final MapService mapService;

    public MapController(final MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/{mapId}")
    public ResponseEntity<MapFindResponse> find(@PathVariable final Long mapId, @LoginEmail final LoginEmailDto loginEmailDto) {
        MapFindResponse mapFindResponse = mapService.findMap(mapId, loginEmailDto);
        return ResponseEntity.ok().body(mapFindResponse);
    }

    @GetMapping
    public ResponseEntity<MapFindAllResponse> findAll(@LoginEmail final LoginEmailDto loginEmailDto) {
        MapFindAllResponse mapFindAllResponse = mapService.findAllMaps(loginEmailDto);
        return ResponseEntity.ok().body(mapFindAllResponse);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody final MapCreateUpdateRequest mapCreateUpdateRequest, @LoginEmail final LoginEmailDto loginEmailDto) {
        MapCreateResponse mapCreateResponse = mapService.saveMap(mapCreateUpdateRequest, loginEmailDto);
        return ResponseEntity.created(URI.create("/api/managers/maps/" + mapCreateResponse.getId()))
                .build();
    }

    @PutMapping("/{mapId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @Valid @RequestBody final MapCreateUpdateRequest mapCreateUpdateRequest,
            @LoginEmail final LoginEmailDto loginEmailDto) {
        mapService.updateMap(mapId, mapCreateUpdateRequest, loginEmailDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{mapId}")
    public ResponseEntity<Void> delete(@PathVariable final Long mapId, @LoginEmail final LoginEmailDto loginEmailDto) {
        mapService.deleteMap(mapId, loginEmailDto);
        return ResponseEntity.noContent().build();
    }
}

