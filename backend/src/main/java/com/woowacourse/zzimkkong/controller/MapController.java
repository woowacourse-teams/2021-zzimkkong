package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.service.MapService;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    @PutMapping("/{mapId}/practice")
    public ResponseEntity<Void> practice(
            @PathVariable final Long mapId,
            @Manager final Member manager){

        mapService.updateMapImage(manager, mapId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{mapId}")
    public ResponseEntity<Void> delete(@Manager Member member, @PathVariable Long mapId) {
        mapService.deleteMap(member, mapId);
        return ResponseEntity.noContent().build();
    }
}

