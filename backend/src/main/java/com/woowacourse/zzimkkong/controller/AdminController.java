package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Group;
import com.woowacourse.zzimkkong.dto.admin.*;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.service.AdminService;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api")
public class AdminController {
    private static final String DEV_URL = "dev.zzimkkong.com";
    private static final String PROD_URL = "zzimkkong.com";

    private final AdminService adminService;
    private final String profile;

    public AdminController(final AdminService adminService,
                           final @Value("${spring.profiles.active}") String profile) {
        this.adminService = adminService;
        this.profile = profile;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final LoginRequest loginRequest) {
        TokenResponse tokenResponse = adminService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/members")
    public ResponseEntity<MembersResponse> members(
            @PageableDefault(value = 20) Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Group group) {
        MembersResponse membersResponse = adminService.findMembers(pageable, search, group);
        return ResponseEntity.ok(membersResponse);
    }

    @PutMapping("/members/{memberId}/group")
    public ResponseEntity<Void> updateMemberGroup(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberGroupUpdateRequest request) {
        adminService.updateMemberGroup(memberId, request.getGroup());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/maps")
    public ResponseEntity<MapsResponse> maps(@PageableDefault(value = 20) Pageable pageable) {
        MapsResponse mapsResponse = adminService.findMaps(pageable);
        return ResponseEntity.ok(mapsResponse);
    }

    @GetMapping("/spaces")
    public ResponseEntity<SpacesResponse> spaces(@PageableDefault(value = 20) Pageable pageable) {
        SpacesResponse spacesResponse = adminService.findSpaces(pageable);
        return ResponseEntity.ok(spacesResponse);
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationsResponse> reservations(@PageableDefault(value = 20) Pageable pageable) {
        ReservationsResponse reservationsResponse = adminService.findReservations(pageable);
        return ResponseEntity.ok(reservationsResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<String> profile() {
        if (profile.equals("dev")) {
            return ResponseEntity.status(HttpStatus.OK).body(DEV_URL);
        }
        if (profile.equals("prod")) {
            return ResponseEntity.status(HttpStatus.OK).body(PROD_URL);
        }
        return ResponseEntity.badRequest().build();
    }
}
