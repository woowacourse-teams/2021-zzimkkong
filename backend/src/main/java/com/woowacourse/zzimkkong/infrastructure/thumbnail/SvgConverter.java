package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface SvgConverter {
    File convertSvgToPngFile(final String mapSvgData, final String fileName);

    void convertSvgToPng(final InputStream inputStream, final OutputStream outputStream);
}
