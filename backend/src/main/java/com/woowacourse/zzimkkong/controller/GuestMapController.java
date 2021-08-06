package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guests/maps")
public class GuestMapController {
    private final MapService mapService;

    public GuestMapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping
    public ResponseEntity<MapFindResponse> find(@RequestParam final String publicMapId) {
        MapFindResponse mapFindResponse = mapService.findMapByPublicMapId(publicMapId);
        return ResponseEntity.ok().body(mapFindResponse);
    }
}
