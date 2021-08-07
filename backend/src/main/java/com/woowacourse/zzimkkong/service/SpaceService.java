package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.*;
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

import static com.woowacourse.zzimkkong.service.MapService.validateManagerOfMap;

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

    public SpaceCreateResponse saveSpace(
            final Long mapId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        Setting setting = getSetting(spaceCreateUpdateRequest);
        Space space = new Space.Builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .setting(setting)
                .map(map)
                .build();
        Space saveSpace = spaces.save(space);
        return SpaceCreateResponse.from(saveSpace);
    }

    @Transactional(readOnly = true)
    public SpaceFindDetailResponse findSpace(
            final Long mapId,
            final Long spaceId,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        Space space = map.getSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindDetailResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(
            final Long mapId,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        List<Space> findAllSpaces = map.getSpaces();
        return SpaceFindAllResponse.from(findAllSpaces);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(
            final Long mapId) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        List<Space> findAllSpaces = map.getSpaces();
        return SpaceFindAllResponse.from(findAllSpaces);
    }

    public void updateSpace(
            final Long mapId,
            final Long spaceId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        Space space = map.getSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        Setting setting = getSetting(spaceCreateUpdateRequest);
        Space updateSpace = new Space.Builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .setting(setting)
                .build();

        space.update(updateSpace);
    }

    public void deleteSpace(
            final Long mapId,
            final Long spaceId,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        Space space = map.getSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateReservationExistence(spaceId);

        spaces.delete(space);
    }

    private Setting getSetting(final SpaceCreateUpdateRequest spaceCreateUpdateRequest) {
        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();

        return new Setting.Builder()
                .availableStartTime(settingsRequest.getAvailableStartTime())
                .availableEndTime(settingsRequest.getAvailableEndTime())
                .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                .reservationEnable(settingsRequest.getReservationEnable())
                .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                .enabledDayOfWeek(settingsRequest.getEnabledDayOfWeek())
                .build();
    }

    private void validateReservationExistence(final Long spaceId) {
        if (reservations.existsBySpaceIdAndEndTimeAfter(spaceId, timeConverter.getNow())) {
            throw new ReservationExistOnSpaceException();
        }
    }
}
