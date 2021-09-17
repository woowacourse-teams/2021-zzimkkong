package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.admin.MapsResponse;
import com.woowacourse.zzimkkong.dto.admin.MembersResponse;
import com.woowacourse.zzimkkong.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String loginPage() {
        return "login";
    }

    @GetMapping("/members")
    public String memberPage() {
        return "member";
    }

    @GetMapping("/maps")
    public String mapPage() {
        return "map";
    }

    @PostMapping("/api/login")
    public ResponseEntity<Void> login(@RequestParam String id, @RequestParam String password) {
        adminService.login(id, password);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/members")
    public ResponseEntity<MembersResponse> member(@PageableDefault(value = 20) Pageable pageable) {
        MembersResponse members = adminService.findMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/api/maps")
    public ResponseEntity<MapsResponse> map(@PageableDefault(value = 20) Pageable pageable) {
        MapsResponse maps = adminService.findMaps(pageable);
        return ResponseEntity.ok(maps);
    }
}
