package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.ThumbnailManager;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.woowacourse.zzimkkong.service.MapService.validateManagerOfMap;

@Service
@Transactional
public class SpaceService {
    private final MemberRepository members;
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;
    private final ThumbnailManager thumbnailManager;

    public SpaceService(
            final MemberRepository members,
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations,
            final ThumbnailManager thumbnailManager) {
        this.members = members;
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.thumbnailManager = thumbnailManager;
    }

    public SpaceCreateResponse saveSpace(
            final Long mapId,
            final SpaceCreateUpdateRequest spaceCreateUpdateRequest,
            final LoginEmail loginEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        Member manager = members.findByEmail(loginEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        validateManagerOfMap(map, manager);

        Setting setting = getSetting(spaceCreateUpdateRequest);
        Space space = Space.builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .setting(setting)
                .map(map)
                .build();
        Space saveSpace = spaces.save(space);

        thumbnailManager.uploadMapThumbnail(spaceCreateUpdateRequest.getMapImageSvg(), map);
        return SpaceCreateResponse.from(saveSpace);
    }

    @Transactional(readOnly = true)
    public SpaceFindDetailResponse findSpace(
            final Long mapId,
            final Long spaceId,
            final LoginEmail loginEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        Member manager = members.findByEmail(loginEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        validateManagerOfMap(map, manager);

        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindDetailResponse.from(space);
    }

    @Transactional(readOnly = true)
    public SpaceFindAllResponse findAllSpace(
            final Long mapId,
            final LoginEmail loginEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        Member manager = members.findByEmail(loginEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
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
            final LoginEmail loginEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        Member manager = members.findByEmail(loginEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        validateManagerOfMap(map, manager);

        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        Setting setting = getSetting(spaceCreateUpdateRequest);
        Space updateSpace = Space.builder()
                .name(spaceCreateUpdateRequest.getName())
                .color(spaceCreateUpdateRequest.getColor())
                .description(spaceCreateUpdateRequest.getDescription())
                .area(spaceCreateUpdateRequest.getArea())
                .setting(setting)
                .build();

        space.update(updateSpace);
        thumbnailManager.uploadMapThumbnail(spaceCreateUpdateRequest.getMapImageSvg(), map);
    }

    public void deleteSpace(
            final Long mapId,
            final Long spaceId,
            final SpaceDeleteRequest spaceDeleteRequest,
            final LoginEmail loginEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        Member manager = members.findByEmail(loginEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        validateManagerOfMap(map, manager);

        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateReservationExistence(spaceId);

        spaces.delete(space);
        thumbnailManager.uploadMapThumbnail(spaceDeleteRequest.getMapImageSvg(), map);
    }

    private Setting getSetting(final SpaceCreateUpdateRequest spaceCreateUpdateRequest) {
        SettingsRequest settingsRequest = spaceCreateUpdateRequest.getSettingsRequest();

        return Setting.builder()
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
        if (reservations.existsBySpaceIdAndEndTimeAfter(spaceId, LocalDateTime.now())) {
            throw new ReservationExistOnSpaceException();
        }
    }
}
