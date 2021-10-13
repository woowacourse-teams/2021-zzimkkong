package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import java.io.InputStream;

public interface StorageUploader {
    String upload(final String directoryName, String uploadFileName, final InputStream inputStream);

    void delete(final String directoryName, final String fileName);
}
