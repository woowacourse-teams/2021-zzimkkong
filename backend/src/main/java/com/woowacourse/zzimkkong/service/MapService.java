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
import com.woowacourse.zzimkkong.infrastructure.S3Uploader;
import com.woowacourse.zzimkkong.infrastructure.SvgConverter;
import com.woowacourse.zzimkkong.infrastructure.TimeConverter;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
@Transactional
public class MapService {
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;
    private final S3Uploader s3Uploader;
    private final SvgConverter svgConverter;
    private final TimeConverter timeConverter;

    public MapService(final MapRepository maps,
                      final SpaceRepository spaces,
                      final ReservationRepository reservations,
                      final S3Uploader s3Uploader,
                      final SvgConverter svgConverter,
                      final TimeConverter timeConverter) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.s3Uploader = s3Uploader;
        this.svgConverter = svgConverter;
        this.timeConverter = timeConverter;
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
        return MapFindResponse.from(map);
    }

    @Transactional(readOnly = true)
    public MapFindAllResponse findAllMaps(final Member manager) {
        List<Map> findMaps = maps.findAllByMember(manager);
        return MapFindAllResponse.from(findMaps);
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
        String thumbnailUrl = s3Uploader.upload("thumbnails", pngFile);
        pngFile.delete();
        return thumbnailUrl;
    }
}
