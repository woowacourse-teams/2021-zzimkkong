package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
import com.woowacourse.zzimkkong.service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/managers/maps")
public class MapController {
    private final MapService mapService;

    public MapController(final MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/{mapId}")
    public ResponseEntity<MapFindResponse> find(@PathVariable final Long mapId, @Manager final LoginEmail loginEmail) {
        MapFindResponse mapFindResponse = mapService.findMap(mapId, loginEmail);
        return ResponseEntity.ok().body(mapFindResponse);
    }

    @GetMapping
    public ResponseEntity<MapFindAllResponse> findAll(@Manager final LoginEmail loginEmail) {
        MapFindAllResponse mapFindAllResponse = mapService.findAllMaps(loginEmail);
        return ResponseEntity.ok().body(mapFindAllResponse);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody final MapCreateUpdateRequest mapCreateUpdateRequest, @Manager final LoginEmail loginEmail) {
        MapCreateResponse mapCreateResponse = mapService.saveMap(mapCreateUpdateRequest, loginEmail);
        return ResponseEntity.created(URI.create("/api/managers/maps/" + mapCreateResponse.getId()))
                .build();
    }

    @PutMapping("/{mapId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @Valid @RequestBody final MapCreateUpdateRequest mapCreateUpdateRequest,
            @Manager final LoginEmail loginEmail) {
        mapService.updateMap(mapId, mapCreateUpdateRequest, loginEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{mapId}")
    public ResponseEntity<Void> delete(@PathVariable final Long mapId, @Manager final LoginEmail loginEmail) {
        mapService.deleteMap(mapId, loginEmail);
        return ResponseEntity.noContent().build();
    }
}

