package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.ReservationService;
import com.woowacourse.zzimkkong.service.SlackService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.Validator.DATE_FORMAT;

@Component
public abstract class ReservationController {
    protected ReservationService reservationService;
    protected SlackService slackService;

    public ReservationController(final ReservationService reservationService, final SlackService slackService) {
        this.reservationService = reservationService;
        this.slackService = slackService;
    }

    //TODO: DTO 추상화 하기
    @PostMapping("/maps/{mapId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable Long mapId,
            @RequestBody @Valid ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(mapId, reservationCreateUpdateRequest);
        return ResponseEntity
                .created(URI.create("/api/maps/" + mapId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @GetMapping("/maps/{mapId}/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDate date) {
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(mapId, date);
        return ResponseEntity.ok().body(reservationFindAllResponse);
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

    @GetMapping("/maps/{mapId}/spaces/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable Long mapId,
            @PathVariable Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDate date) {
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(mapId, spaceId, date);
        return ResponseEntity.ok().body(reservationFindResponse);
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
            @RequestBody @Valid ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        SlackResponse slackResponse = reservationService.updateReservation(mapId, reservationId, reservationCreateUpdateRequest);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }
}
