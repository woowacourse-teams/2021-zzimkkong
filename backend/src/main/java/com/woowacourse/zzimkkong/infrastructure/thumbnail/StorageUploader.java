package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import java.io.File;
import java.io.InputStream;

public interface StorageUploader {
    String upload(final String directoryName, final File uploadFile);

    String upload(final String directoryName, String uploadFileName, final InputStream inputStream);

    void delete(final String directoryName, final String fileName);
}
