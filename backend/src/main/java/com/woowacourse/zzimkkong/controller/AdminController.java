package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.admin.MapsResponse;
import com.woowacourse.zzimkkong.dto.admin.MembersResponse;
import com.woowacourse.zzimkkong.dto.admin.ReservationsResponse;
import com.woowacourse.zzimkkong.dto.admin.SpacesResponse;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.service.AdminService;
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

    public AdminController(final @Value("${spring.profiles.active}") String profile,
                           final AdminService adminService) {
        this.profile = profile;
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final LoginRequest loginRequest) {
        TokenResponse tokenResponse = adminService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/members")
    public ResponseEntity<MembersResponse> members(@PageableDefault(value = 20) Pageable pageable) {
        MembersResponse membersResponse = adminService.findMembers(pageable);
        return ResponseEntity.ok(membersResponse);
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
        System.out.println("!@#!@#!@#" + profile);
        if (profile.equals("dev")) {
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).body(DEV_URL);
        }
        if (profile.equals("prod")) {
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).body(PROD_URL);
        }
        return ResponseEntity.badRequest().build();
    }
}
