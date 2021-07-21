package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.reservation.NoSuchReservationException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProviderReservationService extends ReservationService {
    public ProviderReservationService(MapRepository maps, SpaceRepository spaces, ReservationRepository reservations) {
        super(maps, spaces, reservations);
    }

    public ReservationCreateResponse saveReservation(
            final Long mapId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            final Member manager) {
        validateMapExistence(mapId);
        validateAuthorityOnMap(mapId, manager);

        validateTime(reservationCreateUpdateWithPasswordRequest);
        Space space = spaces.findById(reservationCreateUpdateWithPasswordRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        validateAvailability(space, reservationCreateUpdateWithPasswordRequest);

        Reservation reservation = reservations.save(
                new Reservation.Builder()
                        .startTime(reservationCreateUpdateWithPasswordRequest.getStartDateTime())
                        .endTime(reservationCreateUpdateWithPasswordRequest.getEndDateTime())
                        .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                        .userName(reservationCreateUpdateWithPasswordRequest.getName())
                        .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                        .space(space)
                        .build());

        return ReservationCreateResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findReservation(
            final Long mapId,
            final Long reservationId,
            final Member provider) {
        validateMapExistence(mapId);
        validateAuthorityOnMap(mapId, provider);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        return ReservationResponse.from(reservation);
    }

    public SlackResponse updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            final Member provider) {
        validateMapExistence(mapId);
        validateAuthorityOnMap(mapId, provider);
        validateTime(reservationCreateUpdateRequest);

        Space space = spaces.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        doDirtyCheck(reservation, reservationCreateUpdateRequest, space);
        validateAvailability(space, reservationCreateUpdateRequest, reservation);

        reservation.update(reservationCreateUpdateRequest, space);
        reservations.save(reservation);
        return SlackResponse.from(reservation);
    }

    public SlackResponse deleteReservation(
            final Long mapId,
            final Long reservationId,
            final Member provider) {
        validateMapExistence(mapId);
        validateAuthorityOnMap(mapId, provider);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        reservations.delete(reservation);
        return SlackResponse.from(reservation);
    }

    private void validateAuthorityOnMap(final Long mapId, final Member provider) {
        List<Map> providerMaps = maps.findAllByMember(provider);
        if (noMapsMatch(providerMaps, mapId)) {
            throw new NoAuthorityOnMapException();
        }
    }

    private boolean noMapsMatch(final List<Map> providerMaps, final Long mapId) {
        return providerMaps.stream().noneMatch(map -> map.hasSameId(mapId));
    }
}
