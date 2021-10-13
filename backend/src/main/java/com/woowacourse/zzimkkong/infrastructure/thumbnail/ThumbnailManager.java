package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.woowacourse.zzimkkong.domain.Map;

public interface ThumbnailManager {
    String uploadMapThumbnailInMemory(final String svgData, final Map map);

    void deleteThumbnail(final Map map);
}
