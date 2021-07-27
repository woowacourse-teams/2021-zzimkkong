package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpaceService {
    private final MapRepository maps;
    private final SpaceRepository spaces;

    public SpaceService(final MapRepository mapRepository, final SpaceRepository spaceRepository) {
        this.maps = mapRepository;
        this.spaces = spaceRepository;
    }

    @Transactional(readOnly = true)
    public SpaceFindResponse findSpace(Long mapId, Long spaceId) {
        validateMapExistence(mapId);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindResponse.from(space);
    }

    private void validateMapExistence(Long mapId) {
        if (!maps.existsById(mapId)) {
            throw new NoSuchMapException();
        }
    }

    public SpaceCreateResponse saveSpace(final Long mapId, final SpaceCreateRequest spaceCreateRequest, final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        if (map.isNotOwnedBy(manager)) {
            throw new NoAuthorityOnMapException();
        }

        SettingsRequest settingsRequest = spaceCreateRequest.getSettingsRequest();

        Space space = spaces.save(
                new Space.Builder()
                        .name(spaceCreateRequest.getSpaceName())
                        .textPosition(null)
                        .color(null)
                        .coordinate(null)
                        .map(map)
                        .description(spaceCreateRequest.getDescription())
                        .area(spaceCreateRequest.getArea())
                        .availableStartTime(settingsRequest.getAvailableStartTime())
                        .availableEndTime(settingsRequest.getAvailableEndTime())
                        .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                        .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                        .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                        .reservationEnable(settingsRequest.getReservationEnable())
                        .disabledWeekdays(settingsRequest.getDisabledWeekdays())
                        .mapImage(spaceCreateRequest.getMapImage())
                        .build());
        return SpaceCreateResponse.from(space);
    }
}
