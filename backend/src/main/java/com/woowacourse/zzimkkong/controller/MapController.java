package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.dto.map.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
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
    public ResponseEntity<MapFindResponse> find(@PathVariable final Long mapId, @LoginEmail final LoginUserEmail loginUserEmail) {
        MapFindResponse mapFindResponse = mapService.findMap(mapId, loginUserEmail);
        return ResponseEntity.ok().body(mapFindResponse);
    }

    @GetMapping
    public ResponseEntity<MapFindAllResponse> findAll(@LoginEmail final LoginUserEmail loginUserEmail) {
        MapFindAllResponse mapFindAllResponse = mapService.findAllMaps(loginUserEmail);
        return ResponseEntity.ok().body(mapFindAllResponse);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody final MapCreateUpdateRequest mapCreateUpdateRequest, @LoginEmail final LoginUserEmail loginUserEmail) {
        MapCreateResponse mapCreateResponse = mapService.saveMap(mapCreateUpdateRequest, loginUserEmail);
        return ResponseEntity.created(URI.create("/api/managers/maps/" + mapCreateResponse.getId()))
                .build();
    }

    @PutMapping("/{mapId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @Valid @RequestBody final MapCreateUpdateRequest mapCreateUpdateRequest,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        mapService.updateMap(mapId, mapCreateUpdateRequest, loginUserEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{mapId}")
    public ResponseEntity<Void> delete(@PathVariable final Long mapId, @LoginEmail final LoginUserEmail loginUserEmail) {
        mapService.deleteMap(mapId, loginUserEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{mapId}/notice")
    public ResponseEntity<Void> createNotice(
            @PathVariable final Long mapId,
            @Valid @RequestBody final NoticeCreateRequest noticeCreateRequest,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        mapService.saveNotice(mapId, noticeCreateRequest, loginUserEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{mapId}/slack")
    public ResponseEntity<Void> createSlackUrl(
            @PathVariable final Long mapId,
            @RequestBody final SlackCreateRequest slackCreateRequest,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        mapService.saveSlackUrl(mapId, slackCreateRequest, loginUserEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{mapId}/slack")
    public ResponseEntity<SlackFindResponse> findSlackUrl(
            @PathVariable final Long mapId,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        SlackFindResponse slackFindResponse = mapService.findSlackUrl(mapId, loginUserEmail);
        return ResponseEntity.ok().body(slackFindResponse);
    }
}

