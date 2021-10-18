package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import java.io.InputStream;
import java.io.OutputStream;

public interface SvgConverter {
    void convertSvgToPng(final InputStream inputStream, final OutputStream outputStream);
}
