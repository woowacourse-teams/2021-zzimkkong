package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.service.GuestReservationService;
import com.woowacourse.zzimkkong.service.ReservationService2;
import com.woowacourse.zzimkkong.service.callback.GuestReservationCallback;
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
    private final ReservationService2 reservationService2;
    private final GuestReservationCallback guestCallback;

    public GuestReservationController(final ReservationService2 reservationService2) {
        this.reservationService2 = reservationService2;
        this.guestCallback = new GuestReservationCallback();
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
        ReservationCreateResponse reservationCreateResponse = reservationService2.saveReservation(reservationCreateDto, guestCallback);
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
        ReservationFindAllResponse reservationFindAllResponse = reservationService2.findAllReservations(reservationFindAllDto, guestCallback);
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
        ReservationFindResponse reservationFindResponse = reservationService2.findReservations(reservationFindDto, guestCallback);
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
        ReservationResponse reservationResponse = reservationService2.findReservation(reservationAuthenticationDto, guestCallback);
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
                reservationCreateUpdateWithPasswordRequest
        );
        reservationService2.updateReservation(reservationUpdateDto, guestCallback);
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
        reservationService2.deleteReservation(reservationAuthenticationDto, guestCallback);
        return ResponseEntity.noContent().build();
    }
}
