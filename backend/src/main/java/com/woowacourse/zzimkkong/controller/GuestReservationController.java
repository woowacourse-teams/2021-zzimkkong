package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.domain.UserType;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.ReservationService;
import com.woowacourse.zzimkkong.service.SlackService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATE_FORMAT;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/guests")
public class GuestReservationController {
    private final SlackService slackService;
    private final ReservationService reservationService;

    public GuestReservationController(
            final SlackService slackService,
            final ReservationService reservationService) {
        this.slackService = slackService;
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationInfiniteScrollResponse> findUpcomingReservations(
            @LoginEmail(isOptional = true) final LoginUserEmail loginUserEmail,
            @PageableDefault(sort = {"reservationTime.date"}) final Pageable Pageable) {
        ReservationInfiniteScrollResponse reservationInfiniteScrollResponse = reservationService.findUpcomingReservations(loginUserEmail, Pageable);
        return ResponseEntity.ok().body(reservationInfiniteScrollResponse);
    }

    @GetMapping("/reservations/history")
    public ResponseEntity<ReservationInfiniteScrollResponse> findPreviousReservations(
            @LoginEmail(isOptional = true) final LoginUserEmail loginUserEmail,
            @PageableDefault(sort = {"reservationTime.date"}, direction = Sort.Direction.DESC) final Pageable Pageable) {
        ReservationInfiniteScrollResponse reservationFindPreviousResponse = reservationService.findPreviousReservations(loginUserEmail, Pageable);
        return ResponseEntity.ok().body(reservationFindPreviousResponse);
    }

    @GetMapping("/maps/{mapId}/spaces/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                mapId,
                date);
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(reservationFindAllDto, UserType.GUEST);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/maps/{mapId}/spaces/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                mapId,
                spaceId,
                date
        );
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(reservationFindDto, UserType.GUEST);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @PostMapping("/maps/{mapId}/spaces/{spaceId}/reservations")
    public ResponseEntity<Void> create(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            @LoginEmail(isOptional = true) final LoginUserEmail loginUserEmail) {
        ReservationCreateDto reservationCreateDto = ReservationCreateDto.of(
                mapId,
                spaceId,
                reservationCreateUpdateWithPasswordRequest,
                loginUserEmail);
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(reservationCreateDto, UserType.GUEST);
        slackService.sendCreateMessage(reservationCreateResponse.getSlackResponse());
        return ResponseEntity
                .created(URI.create("/api/guests/maps/" + mapId + "/spaces/" + spaceId + "/reservations/" + reservationCreateResponse.getId()))
                .build();
    }

    @PostMapping("/maps/{mapId}/spaces/{spaceId}/reservations/{reservationId}")
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
        ReservationResponse reservationResponse = reservationService.findReservation(reservationAuthenticationDto, UserType.GUEST);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/maps/{mapId}/spaces/{spaceId}/reservations/{reservationId}")
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
        SlackResponse slackResponse = reservationService.updateReservation(reservationUpdateDto, UserType.GUEST);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/maps/{mapId}/spaces/{spaceId}/reservations/{reservationId}")
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
        SlackResponse slackResponse = reservationService.deleteReservation(reservationAuthenticationDto, UserType.GUEST);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }
}
