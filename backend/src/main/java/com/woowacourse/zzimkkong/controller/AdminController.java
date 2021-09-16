package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.MembersResponse;
import com.woowacourse.zzimkkong.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final String ADMIN_ID = "zzimkkong";
    private static final String ADMIN_PWD = "zzimkkong1!";

    private AdminService adminService;

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

    @PostMapping("/api/login")
    public ResponseEntity<Void> login(@RequestParam String id, @RequestParam String password) {
        if (id.equals(ADMIN_ID) && password.equals(ADMIN_PWD)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/members")
    public ResponseEntity<MembersResponse> member() {
        MembersResponse members = adminService.findMembers();
        return ResponseEntity.ok(members);
    }
}
