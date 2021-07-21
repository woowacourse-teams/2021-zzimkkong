package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationPasswordAuthenticationRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.GuestReservationService;
import com.woowacourse.zzimkkong.service.SlackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/guests/maps/{mapId}")
public class GuestReservationController extends ReservationController<GuestReservationService> {
    public GuestReservationController(final GuestReservationService reservationService, final SlackService slackService) {
        super(reservationService, slackService);
    }

    @PostMapping("/reservations")
    public ResponseEntity<Void> create(
            @PathVariable final Long mapId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(mapId, reservationCreateUpdateWithPasswordRequest);
        return ResponseEntity
                .created(URI.create("/api/guests/maps/" + mapId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @PostMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        ReservationResponse reservationResponse = reservationService.findReservation(mapId, reservationId, reservationPasswordAuthenticationRequest);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        SlackResponse slackResponse = reservationService.updateReservation(mapId, reservationId, reservationCreateUpdateWithPasswordRequest);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        SlackResponse slackResponse = reservationService.deleteReservation(mapId, reservationId, reservationPasswordAuthenticationRequest);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }
}
