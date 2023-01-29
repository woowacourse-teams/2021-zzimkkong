package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.domain.ReservationType;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
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
import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATETIME_FORMAT;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DATE_FORMAT;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

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

    @GetMapping("/non-login/reservations")
    public ResponseEntity<ReservationInfiniteScrollResponse> findUpcomingNonLoginReservations(
            @RequestParam final String userName,
            @RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) final ZonedDateTime searchStartTime,
            @PageableDefault(sort = {"reservationTime.date"}) final Pageable Pageable) {
        ReservationInfiniteScrollResponse reservationInfiniteScrollResponse = reservationService.findUpcomingNonLoginReservations(
                userName,
                TimeZoneUtils.convertToUTC(searchStartTime),
                Pageable);
        return ResponseEntity.ok().body(reservationInfiniteScrollResponse);
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationInfiniteScrollResponse> findUpcomingReservations(
            @LoginEmail final LoginUserEmail loginUserEmail,
            @PageableDefault(sort = {"reservationTime.date"}) final Pageable Pageable) {
        ReservationInfiniteScrollResponse reservationInfiniteScrollResponse = reservationService.findUpcomingReservations(loginUserEmail, Pageable);
        return ResponseEntity.ok().body(reservationInfiniteScrollResponse);
    }

    @GetMapping("/reservations/history")
    public ResponseEntity<ReservationInfiniteScrollResponse> findPreviousReservations(
            @LoginEmail final LoginUserEmail loginUserEmail,
            @PageableDefault(sort = {"reservationTime.date"}, direction = Sort.Direction.DESC) final Pageable Pageable) {
        ReservationInfiniteScrollResponse reservationFindPreviousResponse = reservationService.findPreviousReservations(loginUserEmail, Pageable);
        return ResponseEntity.ok().body(reservationFindPreviousResponse);
    }

    @GetMapping("/maps/{mapId}/spaces/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date,
            @LoginEmail(isOptional = true) final LoginUserEmail loginUserEmail) {
        ReservationFindAllDto reservationFindAllDto = ReservationFindAllDto.of(
                mapId,
                date,
                loginUserEmail,
                ReservationType.Constants.GUEST);
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(reservationFindAllDto);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/maps/{mapId}/spaces/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date,
            @LoginEmail(isOptional = true) final LoginUserEmail loginUserEmail) {
        ReservationFindDto reservationFindDto = ReservationFindDto.of(
                mapId,
                spaceId,
                date,
                loginUserEmail,
                ReservationType.Constants.GUEST);
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(reservationFindDto);
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
                loginUserEmail,
                ReservationType.Constants.GUEST);
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(reservationCreateDto);
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
            @RequestBody @Valid final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest,
            @LoginEmail(isOptional = true) final LoginUserEmail loginUserEmail) {
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                mapId,
                spaceId,
                reservationId,
                reservationPasswordAuthenticationRequest,
                loginUserEmail,
                ReservationType.Constants.GUEST);
        ReservationResponse reservationResponse = reservationService.findReservation(reservationAuthenticationDto);
        return ResponseEntity.ok().body(reservationResponse);
    }

    @PutMapping("/maps/{mapId}/spaces/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            @LoginEmail(isOptional = true) final LoginUserEmail loginUserEmail) {
        ReservationUpdateDto reservationUpdateDto = ReservationUpdateDto.of(
                mapId,
                spaceId,
                reservationId,
                reservationCreateUpdateWithPasswordRequest,
                loginUserEmail,
                ReservationType.Constants.GUEST);
        SlackResponse slackResponse = reservationService.updateReservation(reservationUpdateDto);
        slackService.sendUpdateMessage(slackResponse);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/maps/{mapId}/spaces/{spaceId}/reservations/{reservationId}")
    public ResponseEntity<Void> delete(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @PathVariable final Long reservationId,
            @RequestBody @Valid ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest,
            @LoginEmail(isOptional = true) final LoginUserEmail loginUserEmail) {
        ReservationAuthenticationDto reservationAuthenticationDto = ReservationAuthenticationDto.of(
                mapId,
                spaceId,
                reservationId,
                reservationPasswordAuthenticationRequest,
                loginUserEmail,
                ReservationType.Constants.GUEST);
        SlackResponse slackResponse = reservationService.deleteReservation(reservationAuthenticationDto);
        slackService.sendDeleteMessage(slackResponse);
        return ResponseEntity.noContent().build();
    }
}
