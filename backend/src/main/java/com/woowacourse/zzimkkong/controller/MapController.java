package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/managers/maps")
public class MapController {
    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Manager Member member, @RequestBody MapCreateUpdateRequest mapCreateUpdateRequest) {
        MapCreateResponse mapCreateResponse = mapService.saveMap(member, mapCreateUpdateRequest);
        return ResponseEntity.created(URI.create("/api/managers/maps/" + mapCreateResponse.getId()))
                .build();
    }

    @GetMapping("/{mapId}")
    public ResponseEntity<MapFindResponse> find(@Manager Member member, @PathVariable Long mapId) {
        MapFindResponse mapFindResponse = mapService.findMap(member, mapId);
        return ResponseEntity.ok(mapFindResponse);
    }

    @GetMapping
    public ResponseEntity<MapFindAllResponse> findAll(@Manager Member member) {
        MapFindAllResponse mapFindAllResponse = mapService.findAllMaps(member);
        return ResponseEntity.ok(mapFindAllResponse);
    }

    @PutMapping("/{mapId}")
    public ResponseEntity<Void> update(@Manager Member member, @PathVariable Long mapId, @RequestBody MapCreateUpdateRequest mapCreateUpdateRequest) {
        mapService.updateMap(member, mapId, mapCreateUpdateRequest);
        return ResponseEntity.ok().build();
    }
}

