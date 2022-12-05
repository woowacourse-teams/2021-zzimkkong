package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Settings;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
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

    public SpaceService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
    }

    public SpaceCreateResponse saveSpace(
            final Long mapId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final LoginUserEmail loginUserEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());

        Settings settings = Settings.from(spaceCreateUpdateRequest.getSettings());
        Space space = Space.builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .area(spaceCreateUpdateRequest.getArea())
                .reservationEnable(spaceCreateUpdateRequest.getReservationEnable())
                .spaceSettings(settings)
                .map(map)
                .build();
        Space saveSpace = spaces.save(space);

        map.updateThumbnail(spaceCreateUpdateRequest.getThumbnail());

        return SpaceCreateResponse.from(saveSpace);
    }

    @Transactional(readOnly = true)
    public SpaceFindDetailResponse findSpace(
            final Long mapId,
            final Long spaceId,
            final LoginUserEmail loginUserEmail) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());

        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindDetailResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(
            final Long mapId,
            final LoginUserEmail loginUserEmail) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());

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
            final LoginUserEmail loginUserEmail) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());

        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        Settings updateSettings = Settings.from(spaceCreateUpdateRequest.getSettings());
        Space updateSpace = Space.builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .area(spaceCreateUpdateRequest.getArea())
                .reservationEnable(spaceCreateUpdateRequest.getReservationEnable())
                .spaceSettings(updateSettings)
                .build();

        space.update(updateSpace);

        map.updateThumbnail(spaceCreateUpdateRequest.getThumbnail());
    }

    public void deleteSpace(
            final Long mapId,
            final Long spaceId,
            final SpaceDeleteRequest spaceDeleteRequest,
            final LoginUserEmail loginUserEmail) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());

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
