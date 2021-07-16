package com.woowacourse.zzimkkong.service;

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
}
