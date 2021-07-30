package com.woowacourse.zzimkkong.infrastructure;

import java.io.File;

public interface StorageUploader {
    String upload(final String directoryName, final File uploadFile);
}
