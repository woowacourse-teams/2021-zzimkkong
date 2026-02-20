package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Group;
import com.woowacourse.zzimkkong.dto.group.GroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getGroups() {
        List<GroupResponse> groups = Arrays.stream(Group.values())
                .map(GroupResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(groups);
    }
}
