package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.*;
import com.woowacourse.zzimkkong.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/maps/{mapId}")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Void> create(
            @PathVariable Long mapId,
            @RequestBody @Valid ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(mapId, reservationCreateUpdateRequest);
        return ResponseEntity
                .created(URI.create("/api/maps/" + mapId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @GetMapping("/spaces/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable Long mapId,
            @PathVariable Long spaceId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(mapId, spaceId, date);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable Long mapId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(mapId, date);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long mapId,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        reservationService.deleteReservation(mapId, reservationId, reservationPasswordAuthenticationRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable Long mapId,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        ReservationResponse reservationResponse = reservationService.findReservation(mapId, reservationId, reservationPasswordAuthenticationRequest);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable Long mapId,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        reservationService.updateReservation(mapId, reservationId, reservationCreateUpdateRequest);
        return ResponseEntity.ok().build();
    }
}
