package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Map;

public interface ThumbnailManager {
    String uploadMapThumbnail(final String svgData, final Map map);

    void deleteThumbnail(final Map map);
}
