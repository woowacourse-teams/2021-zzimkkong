package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapCreateRequest;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.repository.MapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MapService {
    private final MapRepository maps;

    public MapService(final MapRepository mapRepository) {
        this.maps = mapRepository;
    }

    public MapCreateResponse saveMap(final Member member, final MapCreateRequest mapCreateRequest) {
        Map saveMap = maps.save(new Map(
                mapCreateRequest.getMapName(),
                mapCreateRequest.getMapDrawing(),
                mapCreateRequest.getMapImage(),
                member));
        return MapCreateResponse.from(saveMap);
    }
}
