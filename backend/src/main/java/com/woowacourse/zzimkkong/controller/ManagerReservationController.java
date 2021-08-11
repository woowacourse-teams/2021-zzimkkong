package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.ManagerReservationService;
import com.woowacourse.zzimkkong.service.ReservationService2;
import com.woowacourse.zzimkkong.service.SlackService;
import com.woowacourse.zzimkkong.service.callback.ManagerReservationCallback;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATE_FORMAT;

@RestController
@RequestMapping("/api/managers/maps/{mapId}/spaces")
public class ManagerReservationController {
    private final ManagerReservationService reservationService;
    private final SlackService slackService;
    private final ReservationService2 reservationService2;
    private final ManagerReservationCallback managerCallback;

    public ManagerReservationController(
            final ManagerReservationService reservationService,
            final SlackService slackService,
            final ReservationService2 reservationService2) {
        this.reservationService = reservationService;
        this.slackService = slackService;
        this.reservationService2 = reservationService2;
        this.managerCallback = new ManagerReservationCallback();
    }

    @PostMapping("/{spaceId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            @Manager final Member manager) {
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                manager);
        ReservationCreateResponse reservationCreateResponse = reservationService2.saveReservation(reservationCreateDto, managerCallback);
        return ResponseEntity
                .created(URI.create("/api/managers/maps/" + mapId + "/spaces/" + spaceId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date,
            @Manager final Member manager) {
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                mapId,
                date,
                manager);
        ReservationFindAllResponse reservationFindAllResponse = reservationService2.findAllReservations(reservationFindAllDto, managerCallback);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date,
            @Manager final Member manager) {
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                mapId,
                spaceId,
                date,
                manager
        );
        ReservationFindResponse reservationFindResponse = reservationService2.findReservations(reservationFindDto, managerCallback);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @GetMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @Manager final Member manager) {
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                mapId,
                spaceId,
                reservationId,
                manager);
        ReservationResponse reservationResponse = reservationService2.findReservation(reservationAuthenticationDto, managerCallback);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            @Manager final Member manager) {
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                mapId,
                spaceId,
                reservationId,
                reservationCreateUpdateRequest,
                manager
        );
        SlackResponse slackResponse = reservationService2.updateReservation(reservationUpdateDto, managerCallback);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @Manager final Member manager) {
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                mapId,
                spaceId,
                reservationId,
                manager);
        SlackResponse slackResponse = reservationService2.deleteReservation(reservationAuthenticationDto, managerCallback);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }
}
