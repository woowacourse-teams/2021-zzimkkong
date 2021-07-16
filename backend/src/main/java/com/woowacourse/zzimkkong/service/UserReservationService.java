package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationPasswordAuthenticationRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserReservationService extends ReservationService {
    public UserReservationService(
            final MapRepository mapRepository,
            final SpaceRepository spaceRepository,
            final ReservationRepository reservationRepository) {
        super(mapRepository, spaceRepository, reservationRepository);
    }

    @Override
    public void deleteReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);
        Reservation reservation = getReservation(reservationId, reservationPasswordAuthenticationRequest.getPassword());
        reservationRepository.delete(reservation);
    }

    @Override
    public ReservationResponse findReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);
        Reservation reservation = getReservation(reservationId, reservationPasswordAuthenticationRequest.getPassword());
        return ReservationResponse.of(reservation);
    }

    @Override
    public void updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        validateMapExistence(mapId);

        validateTime(reservationCreateUpdateRequest);

        Space space = spaceRepository.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = getReservation(reservationId, reservationCreateUpdateRequest.getPassword());
        doDirtyCheck(reservation, reservationCreateUpdateRequest, space);

        validateAvailability(space, reservationCreateUpdateRequest, reservation);

        reservation.update(reservationCreateUpdateRequest, space);
        reservationRepository.save(reservation);
    }
}
