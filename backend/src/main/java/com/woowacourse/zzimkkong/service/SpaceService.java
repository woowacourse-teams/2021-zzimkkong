package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.NoDataToUpdateException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SpaceService {
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;

    public SpaceService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
    }

    public SpaceCreateResponse saveSpace(final Long mapId, final SpaceCreateUpdateRequest spaceCreateUpdateRequest, final Member manager) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();

        Space space = spaces.save(
                new Space.Builder()
                        .name(spaceCreateUpdateRequest.getName())
                        .textPosition(null)
                        .color(null)
                        .coordinate(null)
                        .map(map)
                        .description(spaceCreateUpdateRequest.getDescription())
                        .area(spaceCreateUpdateRequest.getArea())
                        .availableStartTime(settingsRequest.getAvailableStartTime())
                        .availableEndTime(settingsRequest.getAvailableEndTime())
                        .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                        .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                        .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                        .reservationEnable(settingsRequest.getReservationEnable())
                        .disabledWeekdays(settingsRequest.getDisabledWeekdays())
                        .mapImage(spaceCreateUpdateRequest.getMapImage())
                        .build());
        return SpaceCreateResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindDetailResponse findSpace(Long mapId, Long spaceId, Member manager) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindDetailResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(final Long mapId, final Member manager) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        List<Space> spaces = this.spaces.findAllByMapId(mapId);
        return SpaceFindAllResponse.from(spaces);
    }

    public void updateSpace(
            final Long mapId,
            final Long spaceId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final Member manager) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        doDirtyCheck(space, spaceCreateUpdateRequest, map);
        space.update(spaceCreateUpdateRequest, map);
    }

    public void deleteSpace(
            final Long mapId,
            final Long spaceId,
            final Member manager) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateReservationExistence(space);

        spaces.delete(space);
    }

    private void validateReservationExistence(final Space space) {
        if (reservations.existsBySpace(space)) {
            throw new ReservationExistOnSpaceException();
        }
    }

    private void validateAuthorityOnMap(final Member manager, final Map map) {
        if (map.isNotOwnedBy(manager)) {
            throw new NoAuthorityOnMapException();
        }
    }

    private void doDirtyCheck(
            final Space space,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final Map map) {
        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();

        Space updatedSpace = new Space.Builder()
                .name(spaceCreateUpdateRequest.getName())
                .textPosition(null)
                .color(null)
                .coordinate(null)
                .map(map)
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .availableStartTime(settingsRequest.getAvailableStartTime())
                .availableEndTime(settingsRequest.getAvailableEndTime())
                .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                .reservationEnable(settingsRequest.getReservationEnable())
                .disabledWeekdays(settingsRequest.getDisabledWeekdays())
                .mapImage(spaceCreateUpdateRequest.getMapImage())
                .build();

        if (space.hasSameData(updatedSpace)) {
            throw new NoDataToUpdateException();
        }
    }
}
