package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.ReservationDeleteRequest;
import com.woowacourse.zzimkkong.exception.*;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveResponse;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final MapRepository mapRepository;
    private final SpaceRepository spaceRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(MapRepository mapRepository, SpaceRepository spaceRepository, ReservationRepository reservationRepository) {
        this.mapRepository = mapRepository;
        this.spaceRepository = spaceRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationSaveResponse saveReservation(Long mapId, ReservationSaveRequest reservationSaveRequest) {
        mapRepository.findById(mapId).orElseThrow(NoSuchMapException::new);
        Space space = spaceRepository.findById(reservationSaveRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);

        Reservation reservation = reservationRepository.save(
                new Reservation(
                        reservationSaveRequest.getStartDateTime(),
                        reservationSaveRequest.getEndDateTime(),
                        reservationSaveRequest.getPassword(),
                        reservationSaveRequest.getName(),
                        reservationSaveRequest.getDescription(),
                        space)
        );
        return ReservationSaveResponse.of(reservation);
    }

    public void deleteReservation(Long mapId, Long reservationId, ReservationDeleteRequest reservationDeleteRequest) {
        mapRepository.findById(mapId).orElseThrow(NoSuchMapException::new);
        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        if (reservation.isWrongPassword(reservationDeleteRequest.getPassword())) {
            throw new ReservationPasswordException();
        };
        reservationRepository.delete(reservation);
    }
}
