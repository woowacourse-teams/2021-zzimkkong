package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.map.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.map.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class MapService {
    private final MemberRepository members;
    private final MapRepository maps;
    private final ReservationRepository reservations;
    private final SharingIdGenerator sharingIdGenerator;

    public MapService(
            final MemberRepository members,
            final MapRepository maps,
            final ReservationRepository reservations,
            final SharingIdGenerator sharingIdGenerator) {
        this.members = members;
        this.maps = maps;
        this.reservations = reservations;
        this.sharingIdGenerator = sharingIdGenerator;
    }

    public MapCreateResponse saveMap(final MapCreateUpdateRequest mapCreateUpdateRequest, final LoginUserEmail loginUserEmail) {
        Member manager = members.findByEmail(loginUserEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        Map saveMap = maps.save(new Map(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                mapCreateUpdateRequest.getThumbnail(),
                manager));

        return MapCreateResponse.from(saveMap);
    }

    @Transactional(readOnly = true)
    public MapFindResponse findMap(final Long mapId, final LoginUserEmail loginUserEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        validateManagerOfMap(map, loginUserEmail.getEmail());

        map.activateSharingMapId(sharingIdGenerator);

        return MapFindResponse.of(map);
    }

    @Transactional(readOnly = true)
    public MapFindAllResponse findAllMaps(final LoginUserEmail loginUserEmail) {
        Member manager = members.findByEmailWithFetchMaps(loginUserEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);

        List<Map> findMaps = manager.getMaps();

        return findMaps.stream()
                .peek(map -> map.activateSharingMapId(sharingIdGenerator))
                .map(MapFindResponse::of)
                .collect(collectingAndThen(toList(), mapFindResponses -> MapFindAllResponse.of(mapFindResponses, manager)));
    }

    @Transactional(readOnly = true)
    public MapFindResponse findMapBySharingId(final String sharingMapId) {
        Long mapId = sharingIdGenerator.parseIdFrom(sharingMapId);
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        map.activateSharingMapId(sharingIdGenerator);

        return MapFindResponse.of(map);
    }

    public void updateMap(final Long mapId,
                          final MapCreateUpdateRequest mapCreateUpdateRequest,
                          final LoginUserEmail loginUserEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());

        map.update(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing());
        map.updateThumbnail(mapCreateUpdateRequest.getThumbnail());
    }

    public void deleteMap(final Long mapId, final LoginUserEmail loginUserEmail) {
        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);

        validateManagerOfMap(map, loginUserEmail.getEmail());
        validateExistReservations(map);

        maps.delete(map);
    }

    public void saveSlackUrl(final Long mapId,
                             final SlackCreateRequest slackCreateRequest,
                             final LoginUserEmail loginUserEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());
        map.updateSlackUrl(slackCreateRequest.getSlackUrl());
    }

    public void saveNotice(final Long mapId,
                           final NoticeCreateRequest noticeCreateRequest,
                           final LoginUserEmail loginUserEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());
        map.updateNotice(noticeCreateRequest.getNotice());
    }

    @Transactional(readOnly = true)
    public SlackFindResponse findSlackUrl(final Long mapId, final LoginUserEmail loginUserEmail) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, loginUserEmail.getEmail());
        return SlackFindResponse.from(map);
    }

    private void validateExistReservations(final Map map) {
        List<Space> findSpaces = map.getSpaces();

        boolean isExistReservationInAnySpace = findSpaces.stream()
                .anyMatch(space -> reservations.existsBySpaceIdAndReservationTimeEndTimeAfter(space.getId(), LocalDateTime.now()));

        if (isExistReservationInAnySpace) {
            throw new ReservationExistOnSpaceException();
        }
    }

    private void validateManagerOfMap(final Map map, final String email) {
        if (!map.isOwnedBy(email)) {
            throw new NoAuthorityOnMapException();
        }
    }
}
