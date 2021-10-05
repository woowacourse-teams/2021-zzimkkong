package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import java.io.File;

public interface SvgConverter {
    File convertSvgToPngFile(final String mapSvgData, final String fileName);
}
