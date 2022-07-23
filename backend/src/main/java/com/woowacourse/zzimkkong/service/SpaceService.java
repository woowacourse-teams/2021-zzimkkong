package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SettingRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SpaceService {
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;
    private final SettingRepository settings;

    public SpaceService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations,
            final SettingRepository settings) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.settings = settings;
    }

    public SpaceCreateResponse saveSpace(
            final Long mapId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final LoginEmailDto loginEmailDto) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginEmailDto.getEmail());

        Space space = Space.builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .reservationEnable(spaceCreateUpdateRequest.getReservationEnable())
                .map(map)
                .build();
        Space saveSpace = spaces.save(space);

        Settings spaceSettings = Settings.of(saveSpace, spaceCreateUpdateRequest);
        settings.saveAll(spaceSettings.getSettings());

        map.updateThumbnail(spaceCreateUpdateRequest.getThumbnail());

        return SpaceCreateResponse.from(saveSpace);
    }

    @Transactional(readOnly = true)
    public SpaceFindDetailResponse findSpace(
            final Long mapId,
            final Long spaceId,
            final LoginEmailDto loginEmailDto) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginEmailDto.getEmail());

        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindDetailResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(
            final Long mapId,
            final LoginEmailDto loginEmailDto) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginEmailDto.getEmail());

        List<Space> findAllSpaces = map.getSpaces();
        return SpaceFindAllResponse.from(findAllSpaces);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(
            final Long mapId) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);

        List<Space> findAllSpaces = map.getSpaces();
        return SpaceFindAllResponse.from(findAllSpaces);
    }

    public void updateSpace(
            final Long mapId,
            final Long spaceId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final LoginEmailDto loginEmailDto) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginEmailDto.getEmail());

        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        Space updateSpace = Space.builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .reservationEnable(spaceCreateUpdateRequest.getReservationEnable())
                .build();
        Settings updateSpaceSettings = Settings.of(updateSpace, spaceCreateUpdateRequest);

        space.update(updateSpace);

        //TODO: 기존 settings delete 알아서 잘 되는지 테스트 해보기!
        // delete 하나의 쿼리로 날라가는지 확인 할 것
        // update 할 때, orphanRemove true에 의해서 delete 될 것임
        settings.saveAll(updateSpaceSettings.getSettings());

        map.updateThumbnail(spaceCreateUpdateRequest.getThumbnail());
    }

    public void deleteSpace(
            final Long mapId,
            final Long spaceId,
            final SpaceDeleteRequest spaceDeleteRequest,
            final LoginEmailDto loginEmailDto) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginEmailDto.getEmail());

        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateReservationExistence(spaceId);

        spaces.delete(space);

        map.updateThumbnail(spaceDeleteRequest.getThumbnail());
    }

    private void validateReservationExistence(final Long spaceId) {
        if (reservations.existsBySpaceIdAndReservationTimeEndTimeAfter(spaceId, LocalDateTime.now())) {
            throw new ReservationExistOnSpaceException();
        }
    }

    private void validateManagerOfMap(final Map map, final String email) {
        if (!map.isOwnedBy(email)) {
            throw new NoAuthorityOnMapException();
        }
    }
}
