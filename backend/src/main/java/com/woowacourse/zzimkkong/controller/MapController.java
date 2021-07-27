package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateRequest;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
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
    public ResponseEntity<MapFindResponse> find(@PathVariable Long mapId, @Manager Member manager) {
        MapFindResponse mapFindResponse = mapService.findMap(mapId, manager);
        return ResponseEntity.ok(mapFindResponse);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody MapCreateRequest mapCreateRequest, @Manager Member manager) {
        MapCreateResponse mapCreateResponse = mapService.saveMap(mapCreateRequest, manager);
        return ResponseEntity.created(URI.create("/api/managers/maps/" + mapCreateResponse.getId()))
                .build();
    }
}

