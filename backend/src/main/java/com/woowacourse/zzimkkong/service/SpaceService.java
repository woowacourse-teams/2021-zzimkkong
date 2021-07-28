package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;

import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SpaceService {
    private final MapRepository maps;
    private final SpaceRepository spaces;

    public SpaceService(final MapRepository mapRepository, final SpaceRepository spaceRepository) {
        this.maps = mapRepository;
        this.spaces = spaceRepository;
    }

    public SpaceCreateResponse saveSpace(final Long mapId, final SpaceCreateRequest spaceCreateRequest, final Member manager) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateAuthorityOnMap(manager, map);

        SettingsRequest settingsRequest = spaceCreateRequest.getSettingsRequest();

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
                        .name(spaceCreateRequest.getSpaceName())
                        .textPosition(null)
                        .color(null)
                        .coordinate(null)
                        .map(map)
                        .description(spaceCreateRequest.getDescription())
                        .area(spaceCreateRequest.getArea())
                        .setting(setting)
                        .mapImage(spaceCreateRequest.getMapImage())
                        .build());
        return SpaceCreateResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindDetailResponse findSpace(Long mapId, Long spaceId, Member manager) {
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

        List<Space> spaces = this.spaces.findAllByMapId(mapId);
        return SpaceFindAllResponse.from(spaces);
    }

    private void validateAuthorityOnMap(final Member manager, final Map map) {
        if (map.isNotOwnedBy(manager)) {
            throw new NoAuthorityOnMapException();
        }
    }
}
