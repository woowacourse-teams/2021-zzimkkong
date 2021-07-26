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
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MapService {
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;

    public MapService(final MapRepository maps, final SpaceRepository spaces, final ReservationRepository reservations) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
    }

    public MapCreateResponse saveMap(final Member member, final MapCreateUpdateRequest mapCreateUpdateRequest) {
        Map saveMap = maps.save(new Map(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                mapCreateUpdateRequest.getMapImage(),
                member));
        convertSvgToPng(mapCreateUpdateRequest.getMapImage());
        return MapCreateResponse.from(saveMap);
    }

    @Transactional(readOnly = true)
    public MapFindResponse findMap(final Member member, final Long mapId) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        validateManagerOfMap(member, map);
        return MapFindResponse.from(map);
    }

    @Transactional(readOnly = true)
    public MapFindAllResponse findAllMaps(Member member) {
        List<Map> findMaps = maps.findAllByMember(member);
        return MapFindAllResponse.from(findMaps);
    }

    public void updateMap(final Member member, final Long mapId, MapCreateUpdateRequest mapCreateUpdateRequest) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        validateManagerOfMap(member, map);

        convertSvgToPng(mapCreateUpdateRequest.getMapImage());

        map.update(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                mapCreateUpdateRequest.getMapImage());
    }

    public void deleteMap(final Member member, final Long mapId) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);

        validateManagerOfMap(member, map);

        validateExistReservations(mapId);

        maps.deleteById(mapId);
    }

    private void validateExistReservations(Long mapId) {
        List<Space> findSpaces = spaces.findAllByMapId(mapId);

        boolean isExistReservationInAnySpace = findSpaces.stream()
                .anyMatch(space -> reservations.existsBySpaceIdAndEndTimeAfter(space.getId(), LocalDateTime.now()));

        if (isExistReservationInAnySpace) {
            throw new ReservationExistOnSpaceException();
        }
    }

    private void validateManagerOfMap(final Member manager, final Map map) {
        if (!manager.equals(map.getMember())) {   // TODO: ReservationService 와의 중복 제거 -김샐
            throw new NoAuthorityOnMapException();
        }
    }

    public void convertSvgToPng(final String mapSvgData) {
        try {
            ByteArrayInputStream svgInput = new ByteArrayInputStream(mapSvgData.getBytes());
            TranscoderInput transcoderInput = new TranscoderInput(svgInput);

            OutputStream outputStream = new FileOutputStream("/Users/yeonwoocho/Downloads/zzkk.png");
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);

            PNGTranscoder pngTranscoder = new PNGTranscoder();
            pngTranscoder.transcode(transcoderInput, transcoderOutput);

            outputStream.flush();
            outputStream.close();
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }
    }
}
