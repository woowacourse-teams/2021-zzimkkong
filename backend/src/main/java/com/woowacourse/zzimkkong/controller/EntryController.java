package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.dto.space.SpaceEntryResponse;
import com.woowacourse.zzimkkong.service.SpaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/entry")
public class EntryController {
    private final SpaceService spaceService;

    public EntryController(final SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @GetMapping("/spaces")
    public ResponseEntity<SpaceEntryResponse> findSpaceByShareId(
            @RequestParam final String sharingSpaceId) {
        SpaceEntryResponse spaceEntryResponse = spaceService.findSpaceBySharingId(sharingSpaceId);
        return ResponseEntity.ok().body(spaceEntryResponse);
    }
}
