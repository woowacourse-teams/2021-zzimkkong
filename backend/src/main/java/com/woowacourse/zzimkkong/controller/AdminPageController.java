package com.woowacourse.zzimkkong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {
    @GetMapping("/login")
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

    @GetMapping("/spaces")
    public String spacePage() {
        return "space";
    }

    @GetMapping("/reservations")
    public String reservationPage() {
        return "reservation";
    }
}
