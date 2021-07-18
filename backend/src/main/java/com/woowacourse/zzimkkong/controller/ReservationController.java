package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationFindAllResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationFindResponse;
import com.woowacourse.zzimkkong.service.ReservationService;
import com.woowacourse.zzimkkong.service.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.Validator.DATE_FORMAT;

@Component
public abstract class ReservationController<Service extends ReservationService> {
    protected Service reservationService;
    protected SlackService slackService;

    @Autowired(required = false)
    public ReservationController(final Service reservationService, final SlackService slackService) {
        this.reservationService = reservationService;
        this.slackService = slackService;
    }

    @GetMapping("/maps/{mapId}/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(mapId, date);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/maps/{mapId}/spaces/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(mapId, spaceId, date);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @PostMapping("/maps/{mapId}/reservations")
    public abstract ResponseEntity<Void> create(
            final Long mapId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest);
}
