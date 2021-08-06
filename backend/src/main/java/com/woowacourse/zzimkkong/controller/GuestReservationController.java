package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.service.GuestReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATE_FORMAT;

@RestController
@RequestMapping("/api/guests/maps/{mapId}/spaces")
public class GuestReservationController {
    private final GuestReservationService reservationService;

    public GuestReservationController(final GuestReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{spaceId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest);
        return ResponseEntity
                .created(URI.create("/api/guests/maps/" + mapId + "/spaces/" + spaceId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(mapId, date);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(mapId, spaceId, date);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @PostMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        ReservationResponse reservationResponse = reservationService.findReservation(
                mapId,
                spaceId,
                reservationId,
                reservationPasswordAuthenticationRequest);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        reservationService.updateReservation(
                mapId,
                spaceId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        reservationService.deleteReservation(
                mapId,
                spaceId,
                reservationId,
                reservationPasswordAuthenticationRequest);
        return ResponseEntity.noContent().build();
    }
}
