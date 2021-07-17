package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationPasswordAuthenticationRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.SlackService;
import com.woowacourse.zzimkkong.service.UserReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserReservationController extends ReservationController<UserReservationService> {
    public UserReservationController(final UserReservationService reservationService, final SlackService slackService) {
        super(reservationService, slackService);
    }

    @DeleteMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long mapId,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        SlackResponse slackResponse = reservationService.deleteReservation(mapId, reservationId, reservationPasswordAuthenticationRequest);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable Long mapId,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        ReservationResponse reservationResponse = reservationService.findReservation(mapId, reservationId, reservationPasswordAuthenticationRequest);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable Long mapId,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        SlackResponse slackResponse = reservationService.updateReservation(mapId, reservationId, reservationCreateUpdateWithPasswordRequest);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }
}
