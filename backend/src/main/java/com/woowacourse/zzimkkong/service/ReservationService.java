package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveResponse;
import com.woowacourse.zzimkkong.exception.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
        mapRepository.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        Space space = spaceRepository.findById(reservationSaveRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);

        Reservation reservation = reservationRepository.save(
                Reservation.from(reservationSaveRequest, space)
        );

        return ReservationSaveResponse.of(reservation);
    }
}
