package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.ReservationDeleteRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveResponse;
import com.woowacourse.zzimkkong.service.MapService;
import com.woowacourse.zzimkkong.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class ReservationController {
    private final MapService mapService;
    private final ReservationService reservationService;

    public ReservationController(MapService mapService, ReservationService reservationService) {
        this.mapService = mapService;
        this.reservationService = reservationService;
    }

    @PostMapping("/maps/{mapId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable Long mapId,
            @RequestBody ReservationSaveRequest reservationSaveRequest) {

        ReservationSaveResponse reservationSaveResponse = reservationService.saveReservation(mapId, reservationSaveRequest);
        return ResponseEntity
                .created(URI.create("/api/maps/"+mapId+"reservations"+reservationSaveResponse.getId()))
                .build();
    }

    @DeleteMapping("/maps/{mapId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long mapId,
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationDeleteRequest reservationDeleteRequest) {
        reservationService.deleteReservation(mapId, reservationId, reservationDeleteRequest);
        return ResponseEntity.noContent().build();
    }
}
