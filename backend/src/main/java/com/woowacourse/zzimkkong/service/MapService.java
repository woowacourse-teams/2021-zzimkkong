package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.infrastructure.S3Uploader;
import com.woowacourse.zzimkkong.infrastructure.SvgConverter;
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

    public MapService(final MapRepository maps,
                      final SpaceRepository spaces,
                      final ReservationRepository reservations,
                      final S3Uploader s3Uploader,
                      final SvgConverter svgConverter) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.s3Uploader = s3Uploader;
        this.svgConverter = svgConverter;
    }

    public MapCreateResponse saveMap(final MapCreateUpdateRequest mapCreateUpdateRequest, final Member manager) {
        Map saveMap = maps.save(new Map(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                mapCreateUpdateRequest.getMapSvg().substring(10),
                manager));

        String thumbnailUrl = uploadPngToS3(saveMap.getId().toString(), mapCreateUpdateRequest.getMapSvg());
        saveMap.updateImage(thumbnailUrl);

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

        uploadPngToS3(map.getId().toString(), mapCreateUpdateRequest.getMapSvg());

        map.update(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                mapCreateUpdateRequest.getMapSvg());
    }

    private void validateManagerOfMap(final Map map, final Member manager) {
        if (!manager.equals(map.getMember())) {   // TODO: ReservationService 와의 중복 제거 -김샐
            throw new NoAuthorityOnMapException();
        }
    }

    private String uploadPngToS3(String fileName, String svg) {
        File pngFile = svgConverter.convertSvgToPng(fileName, svg);
        String thumbnailUrl = s3Uploader.upload("thumbnails", pngFile);
        pngFile.delete();
        return thumbnailUrl;
    }
}
