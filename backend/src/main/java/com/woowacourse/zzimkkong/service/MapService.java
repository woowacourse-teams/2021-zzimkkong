package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.InvalidAccessLinkException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.infrastructure.PublicIdGenerator;
import com.woowacourse.zzimkkong.infrastructure.StorageUploader;
import com.woowacourse.zzimkkong.infrastructure.SvgConverter;
import com.woowacourse.zzimkkong.infrastructure.TimeConverter;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class MapService {
    public static final String THUMBNAILS_DIRECTORY_NAME = "thumbnails";
    public static final String THUMBNAIL_EXTENSION = ".png";

    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;
    private final StorageUploader storageUploader;
    private final SvgConverter svgConverter;
    private final TimeConverter timeConverter;
    private final PublicIdGenerator publicIdGenerator;

    public MapService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations,
            final StorageUploader storageUploader,
            final SvgConverter svgConverter,
            final TimeConverter timeConverter,
            final PublicIdGenerator publicIdGenerator) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.storageUploader = storageUploader;
        this.svgConverter = svgConverter;
        this.timeConverter = timeConverter;
        this.publicIdGenerator = publicIdGenerator;
    }

    public MapCreateResponse saveMap(final MapCreateUpdateRequest mapCreateUpdateRequest, final Member manager) {
        Map saveMap = maps.save(new Map(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                mapCreateUpdateRequest.getMapImageSvg().substring(0, 10),
                manager));

        String thumbnailUrl = uploadPngToS3(mapCreateUpdateRequest.getMapImageSvg(), saveMap.getId().toString());
        saveMap.updateImageUrl(thumbnailUrl);

        return MapCreateResponse.from(saveMap);
    }

    @Transactional(readOnly = true)
    public MapFindResponse findMap(final Long mapId, final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        validateManagerOfMap(map, manager);
        return MapFindResponse.of(map, publicIdGenerator.from(map));
    }

    @Transactional(readOnly = true)
    public MapFindAllResponse findAllMaps(final Member manager) {
        List<Map> findMaps = maps.findAllByMember(manager);
        return findMaps.stream()
                .map(map -> MapFindResponse.of(map, publicIdGenerator.from(map)))
                .collect(collectingAndThen(toList(), mapFindResponses -> MapFindAllResponse.of(mapFindResponses, manager)));
    }

    public void updateMap(final Long mapId, final MapCreateUpdateRequest mapCreateUpdateRequest, final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        validateManagerOfMap(map, manager);

        String thumbnailUrl = uploadPngToS3(mapCreateUpdateRequest.getMapImageSvg(), map.getId().toString());

        map.update(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                thumbnailUrl);
    }

    public void deleteMap(final Long mapId, final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        validateManagerOfMap(map, manager);

        validateExistReservations(mapId);

        maps.deleteById(mapId);

        deleteThumbnail(map);
    }

    private void validateExistReservations(Long mapId) {
        List<Space> findSpaces = spaces.findAllByMapId(mapId);

        boolean isExistReservationInAnySpace = findSpaces.stream()
                .anyMatch(space -> reservations.existsBySpaceIdAndEndTimeAfter(space.getId(), timeConverter.getNow()));

        if (isExistReservationInAnySpace) {
            throw new ReservationExistOnSpaceException();
        }
    }

    private void validateManagerOfMap(final Map map, final Member manager) {
        if (!manager.equals(map.getMember())) {   // TODO: ReservationService 와의 중복 제거 -김샐
            throw new NoAuthorityOnMapException();
        }
    }

    private String uploadPngToS3(final String svgData, final String fileName) {
        File pngFile = svgConverter.convertSvgToPngFile(svgData, fileName);
        String thumbnailUrl = storageUploader.upload(THUMBNAILS_DIRECTORY_NAME, pngFile);
        pngFile.delete();
        return thumbnailUrl;
    }

    private void deleteThumbnail(Map map) {
        String fileName = map.getId().toString();
        storageUploader.delete(THUMBNAILS_DIRECTORY_NAME, fileName + THUMBNAIL_EXTENSION);
    }

    public MapFindResponse findMapByPublicMapId(String publicMapId) {
        try {
            Long mapId = publicIdGenerator.parseIdFrom(publicMapId);
            Map map = maps.findById(mapId)
                    .orElseThrow(InvalidAccessLinkException::new);
            return MapFindResponse.of(map, publicIdGenerator.from(map));
        } catch (NumberFormatException exception) {
            throw new InvalidAccessLinkException();
        }
    }
}
