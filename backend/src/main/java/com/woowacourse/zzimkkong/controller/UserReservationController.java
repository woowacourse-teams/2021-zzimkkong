package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.service.SlackService;
import com.woowacourse.zzimkkong.service.UserReservationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserReservationController extends ReservationController {
    public UserReservationController(final UserReservationService userReservationService, final SlackService slackService) {
        super(userReservationService, slackService);
    }
}
