package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapCreateRequest;
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

    public MapCreateResponse saveMap(final MapCreateRequest mapCreateRequest, final Member member) {
        Map saveMap = maps.save(new Map(
                mapCreateRequest.getMapName(),
                mapCreateRequest.getMapDrawing(),
                mapCreateRequest.getMapImage(),
                member));
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
    public MapFindAllResponse findAllMaps(Member member) {
        List<Map> findMaps = maps.findAllByMember(member);
        return MapFindAllResponse.from(findMaps);
    }

    private void validateManagerOfMap(Map map, Member manager) {
        if(!manager.equals(map.getMember())) {   // TODO: ReservationService 와의 중복 제거 -김샐
            throw new NoAuthorityOnMapException();
        }
    }
}
