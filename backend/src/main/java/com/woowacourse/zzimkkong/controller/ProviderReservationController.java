package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Provider;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.infrastructure.AuthorizationExtractor;
import com.woowacourse.zzimkkong.infrastructure.JwtUtils;
import com.woowacourse.zzimkkong.service.ProviderReservationService;
import com.woowacourse.zzimkkong.service.SlackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/providers")
public class ProviderReservationController extends ReservationController<ProviderReservationService> {
    public ProviderReservationController(final ProviderReservationService reservationService, final SlackService slackService) {
        super(reservationService, slackService);
    }

    @Override
    public ResponseEntity<Void> create(
            @PathVariable final Long mapId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(mapId, reservationCreateUpdateWithPasswordRequest);
        return ResponseEntity
                .created(URI.create("/api/providers/maps/" + mapId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @GetMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @Provider final Member provider) {
        ReservationResponse reservationResponse = reservationService.findReservation(mapId, reservationId, provider);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            @Provider final Member provider) {
        SlackResponse slackResponse = reservationService.updateReservation(
                mapId,
                reservationId,
                reservationCreateUpdateRequest,
                provider);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @Provider final Member provider) {
        SlackResponse slackResponse = reservationService.deleteReservation(mapId, reservationId, provider);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }
}
