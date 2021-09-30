package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.admin.MapsResponse;
import com.woowacourse.zzimkkong.dto.admin.MembersResponse;
import com.woowacourse.zzimkkong.dto.admin.ReservationsResponse;
import com.woowacourse.zzimkkong.dto.admin.SpacesResponse;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
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
}
