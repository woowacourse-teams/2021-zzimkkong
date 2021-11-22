package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.ReservationService;
import com.woowacourse.zzimkkong.service.slack.SlackService;
import com.woowacourse.zzimkkong.service.strategy.GuestReservationStrategy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATE_FORMAT;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/guests/maps/{mapId}/spaces")
public class GuestReservationController {
    private final SlackService slackService;
    private final ReservationService reservationService;
    private final GuestReservationStrategy guestReservationStrategy;

    public GuestReservationController(
            final SlackService slackService,
            final ReservationService reservationService) {
        this.slackService = slackService;
        this.reservationService = reservationService;
        this.guestReservationStrategy = new GuestReservationStrategy();
    }

    @PostMapping("/{spaceId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest);
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(reservationCreateDto, guestReservationStrategy);
        slackService.sendCreateMessage(reservationCreateResponse.getSlackResponse());
        return ResponseEntity
                .created(URI.create("/api/guests/maps/" + mapId + "/spaces/" + spaceId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                mapId,
                date);
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(reservationFindAllDto, guestReservationStrategy);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                mapId,
                spaceId,
                date
        );
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(reservationFindDto, guestReservationStrategy);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @PostMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                mapId,
                spaceId,
                reservationId,
                reservationPasswordAuthenticationRequest);
        ReservationResponse reservationResponse = reservationService.findReservation(reservationAuthenticationDto, guestReservationStrategy);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                mapId,
                spaceId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);
        SlackResponse slackResponse = reservationService.updateReservation(reservationUpdateDto, guestReservationStrategy);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                mapId,
                spaceId,
                reservationId,
                reservationPasswordAuthenticationRequest);
        SlackResponse slackResponse = reservationService.deleteReservation(reservationAuthenticationDto, guestReservationStrategy);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }
}
