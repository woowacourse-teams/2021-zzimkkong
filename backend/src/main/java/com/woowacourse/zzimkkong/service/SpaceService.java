package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.infrastructure.TimeConverter;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpaceService {
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;
    private final TimeConverter timeConverter;

    public SpaceService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations,
            final TimeConverter timeConverter) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.timeConverter = timeConverter;
    }

    public SpaceCreateResponse saveSpace(final Long mapId, final SpaceCreateUpdateRequest spaceCreateUpdateRequest, final Member manager) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();

        Setting setting = new Setting.Builder()
                .availableStartTime(settingsRequest.getAvailableStartTime())
                .availableEndTime(settingsRequest.getAvailableEndTime())
                .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                .reservationEnable(settingsRequest.getReservationEnable())
                .disabledWeekdays(settingsRequest.getDisabledWeekdays())
                .build();

        Space space = spaces.save(
                new Space.Builder()
                        .name(spaceCreateUpdateRequest.getSpaceName())
                        .textPosition(null)
                        .color(null)
                        .coordinate(null)
                        .map(map)
                        .description(spaceCreateUpdateRequest.getDescription())
                        .area(spaceCreateUpdateRequest.getArea())
                        .setting(setting)
                        .mapImage(spaceCreateUpdateRequest.getMapImage())
                        .build());
        return SpaceCreateResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindDetailResponse findSpace(final Long mapId, final Long spaceId, final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindDetailResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(final Long mapId, final Member manager) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        List<Space> findAllSpaces = spaces.findAllByMapId(mapId);
        return SpaceFindAllResponse.from(findAllSpaces);
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
        Space updateSpace = getUpdateSpace(spaceCreateUpdateRequest, map);

        space.update(updateSpace);
    }

    public void deleteSpace(
            final Long mapId,
            final Long spaceId,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateReservationExistence(spaceId);

        spaces.delete(space);
    }

    private Space getUpdateSpace(final SpaceCreateUpdateRequest spaceCreateUpdateRequest, final Map map) {
        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();

        Setting updateSetting = new Setting.Builder()
                .availableStartTime(settingsRequest.getAvailableStartTime())
                .availableEndTime(settingsRequest.getAvailableEndTime())
                .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                .reservationEnable(settingsRequest.getReservationEnable())
                .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                .disabledWeekdays(settingsRequest.getDisabledWeekdays())
                .build();

        return new Space.Builder()
                .name(spaceCreateUpdateRequest.getSpaceName())
                .map(map)
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .setting(updateSetting)
                .mapImage(spaceCreateUpdateRequest.getMapImage())
                .build();
    }

    private void validateReservationExistence(final Long spaceId) {
        if (reservations.existsBySpaceIdAndEndTimeAfter(spaceId, timeConverter.getNow())) {
            throw new ReservationExistOnSpaceException();
        }
    }

    private void validateAuthorityOnMap(final Member manager, final Map map) {
        if (map.isNotOwnedBy(manager)) {
            throw new NoAuthorityOnMapException();
        }
    }
}
