package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MapService {
    private final MapRepository maps;

    public MapService(final MapRepository mapRepository) {
        this.maps = mapRepository;
    }

    public MapCreateResponse saveMap(final Member member, final MapCreateUpdateRequest mapCreateUpdateRequest) {
        Map saveMap = maps.save(new Map(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                mapCreateUpdateRequest.getMapImage(),
                member));
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

        map.update(
                mapCreateUpdateRequest.getMapName(),
                mapCreateUpdateRequest.getMapDrawing(),
                mapCreateUpdateRequest.getMapImage());
    }

    private void validateManagerOfMap(final Member manager,final Map map) {
        if(!manager.equals(map.getMember())) {   // TODO: ReservationService 와의 중복 제거 -김샐
            throw new NoAuthorityOnMapException();
        }
    }
}
