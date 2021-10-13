package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.woowacourse.zzimkkong.domain.Map;

public interface ThumbnailManager {
    String uploadMapThumbnail(final String svgData, final Map map);

    String uploadMapThumbnailInMemory(final String svgData, final Map map);

    void deleteThumbnail(final Map map);
}
