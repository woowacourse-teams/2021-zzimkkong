package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.SpaceFindResponse;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpaceService {
    private final MapRepository maps;
    private final SpaceRepository spaces;

    public SpaceService(final MapRepository mapRepository, final SpaceRepository spaceRepository) {
        this.maps = mapRepository;
        this.spaces = spaceRepository;
    }

    @Transactional(readOnly = true)
    public SpaceFindResponse findSpace(Long mapId, Long spaceId) {
        validateMapExistence(mapId);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        return SpaceFindResponse.from(space);
    }

    private void validateMapExistence(Long mapId) {
        if (!maps.existsById(mapId)) {
            throw new NoSuchMapException();
        }
    }
}
