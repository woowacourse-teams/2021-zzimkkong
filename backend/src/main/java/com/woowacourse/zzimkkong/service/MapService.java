package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import com.woowacourse.zzimkkong.infrastructure.SharingIdGenerator;
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
    private final ReservationRepository reservations;
    private final StorageUploader storageUploader;
    private final SvgConverter svgConverter;
    private final TimeConverter timeConverter;
    private final SharingIdGenerator sharingIdGenerator;

    public MapService(
            final MapRepository maps,
            final ReservationRepository reservations,
            final StorageUploader storageUploader,
            final SvgConverter svgConverter,
            final TimeConverter timeConverter,
            final SharingIdGenerator sharingIdGenerator) {
        this.maps = maps;
        this.reservations = reservations;
        this.storageUploader = storageUploader;
        this.svgConverter = svgConverter;
        this.timeConverter = timeConverter;
        this.sharingIdGenerator = sharingIdGenerator;
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
        return MapFindResponse.of(map, sharingIdGenerator.from(map));
    }

    @Transactional(readOnly = true)
    public MapFindAllResponse findAllMaps(final Member manager) {
        List<Map> findMaps = maps.findAllByMember(manager);
        return findMaps.stream()
                .map(map -> MapFindResponse.of(map, sharingIdGenerator.from(map)))
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

        //todo 공간-예약 양방향 매핑 적용 후 map 안으로 메서드 옮기기
        validateExistReservations(map);

        maps.delete(map);

        deleteThumbnail(map);
    }

    private void validateExistReservations(final Map map) {
        List<Space> findSpaces = map.getSpaces();

        boolean isExistReservationInAnySpace = findSpaces.stream()
                .anyMatch(space -> reservations.existsBySpaceIdAndEndTimeAfter(space.getId(), timeConverter.getNow()));

        if (isExistReservationInAnySpace) {
            throw new ReservationExistOnSpaceException();
        }
    }

    public static void validateManagerOfMap(final Map map, final Member manager) {
        if (map.isNotOwnedBy(manager)) {
            throw new NoAuthorityOnMapException();
        }
    }

    private String uploadPngToS3(final String svgData, final String fileName) {
        File pngFile = svgConverter.convertSvgToPngFile(svgData, fileName);
        String thumbnailUrl = storageUploader.upload(THUMBNAILS_DIRECTORY_NAME, pngFile);
        pngFile.delete();
        return thumbnailUrl;
    }

    private void deleteThumbnail(final Map map) {
        String fileName = map.getId().toString();
        storageUploader.delete(THUMBNAILS_DIRECTORY_NAME, fileName + THUMBNAIL_EXTENSION);
    }

    public MapFindResponse findMapBySharingId(String sharingMapId) {
        Long mapId = sharingIdGenerator.parseIdFrom(sharingMapId);
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        return MapFindResponse.of(map, sharingIdGenerator.from(map));
    }
}
