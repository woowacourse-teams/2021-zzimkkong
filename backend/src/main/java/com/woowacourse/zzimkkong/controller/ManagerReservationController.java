package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.ManagerReservationService;
import com.woowacourse.zzimkkong.service.SlackService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.Validator.DATE_FORMAT;

@RestController
@RequestMapping("/api/managers/maps/{mapId}")
public class ManagerReservationController {
    private final ManagerReservationService reservationService;
    private final SlackService slackService;

    public ManagerReservationController(final ManagerReservationService reservationService, final SlackService slackService) {
        this.reservationService = reservationService;
        this.slackService = slackService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Void> create(
            @PathVariable final Long mapId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            @Manager final Member manager) {
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(
                mapId,
                reservationCreateUpdateWithPasswordRequest,
                manager);
        return ResponseEntity
                .created(URI.create("/api/managers/maps/" + mapId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date,
            @Manager final Member manager) {
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(mapId, date, manager);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/spaces/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date,
            @Manager final Member manager) {
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(mapId, spaceId, date, manager);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @Manager final Member manager) {
        ReservationResponse reservationResponse = reservationService.findReservation(mapId, reservationId, manager);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            @Manager final Member manager) {
        SlackResponse slackResponse = reservationService.updateReservation(
                mapId,
                reservationId,
                reservationCreateUpdateRequest,
                manager);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long reservationId,
            @Manager final Member manager) {
        SlackResponse slackResponse = reservationService.deleteReservation(mapId, reservationId, manager);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }
}
