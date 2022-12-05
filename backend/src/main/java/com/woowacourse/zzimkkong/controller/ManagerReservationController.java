package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.domain.UserType;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.ReservationService;
import com.woowacourse.zzimkkong.service.SlackService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATE_FORMAT;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/managers/maps/{mapId}/spaces")
public class ManagerReservationController {
    private final SlackService slackService;
    private final ReservationService reservationService;

    public ManagerReservationController(
            final SlackService slackService,
            final ReservationService reservationService) {
        this.slackService = slackService;
        this.reservationService = reservationService;
    }

    @PostMapping("/{spaceId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                loginUserEmail);
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(reservationCreateDto, UserType.MANAGER);
        slackService.sendCreateMessage(reservationCreateResponse.getSlackResponse());
        return ResponseEntity
                .created(URI.create("/api/managers/maps/" + mapId + "/spaces/" + spaceId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                mapId,
                date,
                loginUserEmail);
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(reservationFindAllDto, UserType.MANAGER);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                mapId,
                spaceId,
                date,
                loginUserEmail);
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(reservationFindDto, UserType.MANAGER);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @GetMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> findOne(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                mapId,
                spaceId,
                reservationId,
                loginUserEmail);
        ReservationResponse reservationResponse = reservationService.findReservation(reservationAuthenticationDto, UserType.MANAGER);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                mapId,
                spaceId,
                reservationId,
                reservationCreateUpdateRequest,
                loginUserEmail);
        SlackResponse slackResponse = reservationService.updateReservation(reservationUpdateDto, UserType.MANAGER);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                mapId,
                spaceId,
                reservationId,
                loginUserEmail);
        SlackResponse slackResponse = reservationService.deleteReservation(reservationAuthenticationDto, UserType.MANAGER);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }
}
