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
import com.woowacourse.zzimkkong.infrastructure.ThumbnailManager;
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
    private final ThumbnailManager thumbnailManager;

    public SpaceService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations,
            final TimeConverter timeConverter,
            final ThumbnailManager thumbnailManager) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.timeConverter = timeConverter;
        this.thumbnailManager = thumbnailManager;
    }

    public SpaceCreateResponse saveSpace(
            final Long mapId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();

        Setting setting = new Setting.Builder()
                .availableStartTime(settingsRequest.getAvailableStartTime())
                .availableEndTime(settingsRequest.getAvailableEndTime())
                .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                .reservationEnable(settingsRequest.getReservationEnable())
                .enabledDayOfWeek(settingsRequest.getEnabledDayOfWeek())
                .build();

        Space space = spaces.save(
                new Space.Builder()
                        .name(spaceCreateUpdateRequest.getName())
                        .color(spaceCreateUpdateRequest.getColor())
                        .description(spaceCreateUpdateRequest.getDescription())
                        .area(spaceCreateUpdateRequest.getArea())
                        .setting(setting)
                        .map(map)
                        .textPosition(null)
                        .coordinate(null)
                        .build());

        String thumbnailUrl = thumbnailManager.uploadMapThumbnail(spaceCreateUpdateRequest.getMapImageSvg(), map);
        map.updateImageUrl(thumbnailUrl);

        return SpaceCreateResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindDetailResponse findSpace(
            final Long mapId,
            final Long spaceId,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindDetailResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(
            final Long mapId,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        List<Space> findAllSpaces = spaces.findAllByMapId(mapId);
        return SpaceFindAllResponse.from(findAllSpaces);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(
            final Long mapId) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        List<Space> findAllSpaces = spaces.findAllByMapId(mapId);
        return SpaceFindAllResponse.from(findAllSpaces);
    }

    public void updateSpace(
            final Long mapId,
            final Long spaceId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        Space updateSpace = getUpdateSpace(spaceCreateUpdateRequest, map);

        space.update(updateSpace);

        String thumbnailUrl = thumbnailManager.uploadMapThumbnail(spaceCreateUpdateRequest.getMapImageSvg(), map);
        map.updateImageUrl(thumbnailUrl);
    }

    public void deleteSpace(
            final Long mapId,
            final Long spaceId,
            final SpaceDeleteRequest spaceDeleteRequest,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateReservationExistence(spaceId);

        spaces.delete(space);

        thumbnailManager.uploadMapThumbnail(spaceDeleteRequest.getMapImageSvg(), map);
    }

    private Space getUpdateSpace(
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final Map map) {
        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();

        Setting updateSetting = new Setting.Builder()
                .availableStartTime(settingsRequest.getAvailableStartTime())
                .availableEndTime(settingsRequest.getAvailableEndTime())
                .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                .reservationEnable(settingsRequest.getReservationEnable())
                .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                .enabledDayOfWeek(settingsRequest.getEnabledDayOfWeek())
                .build();

        return new Space.Builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .setting(updateSetting)
                .map(map)
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
