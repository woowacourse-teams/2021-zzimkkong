package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.ReservationFindResponse;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveResponse;
import com.woowacourse.zzimkkong.service.MapService;
import com.woowacourse.zzimkkong.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class ReservationController {
    private MapService mapService;
    private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/maps/{mapId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable Long mapId,
            @RequestBody ReservationSaveRequest reservationSaveRequest) {

        ReservationSaveResponse reservationSaveResponse = reservationService.saveReservation(mapId, reservationSaveRequest);
        return ResponseEntity
                .created(URI.create("/api/maps/" + mapId + "reservations" + reservationSaveResponse.getId()))
                .build();
    }

    @GetMapping("/maps/{mapId}/reservations")
    public ResponseEntity<ReservationFindResponse> read(
            @PathVariable Long mapId,
            @RequestParam Long spaceId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        ReservationFindResponse reservationFindResponse = reservationService.find(mapId, spaceId, date);
        return ResponseEntity.ok().body(reservationFindResponse);
    }
}
