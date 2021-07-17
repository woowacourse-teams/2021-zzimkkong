package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.ProviderReservationService;
import com.woowacourse.zzimkkong.service.SlackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/providers")
public class ProviderReservationController extends ReservationController<ProviderReservationService> {
    public ProviderReservationController(final ProviderReservationService reservationService, final SlackService slackService) {
        super(reservationService, slackService);
    }

    @PostMapping("/maps/{mapId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable Long mapId,
            @RequestBody @Valid ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(mapId, reservationCreateUpdateWithPasswordRequest);
        return ResponseEntity
                .created(URI.create("/api/providers/maps/" + mapId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @DeleteMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long mapId,
            @PathVariable Long reservationId) {
        SlackResponse slackResponse = reservationService.deleteReservation(mapId, reservationId);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable Long mapId,
            @PathVariable Long reservationId) {
        ReservationResponse reservationResponse = reservationService.findReservation(mapId, reservationId);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable Long mapId,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        SlackResponse slackResponse = reservationService.updateReservation(mapId, reservationId, reservationCreateUpdateRequest);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }
}
