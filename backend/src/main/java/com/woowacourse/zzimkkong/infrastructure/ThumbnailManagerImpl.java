package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.StorageUploader;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.SvgConverter;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.ThumbnailManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ThumbnailManagerImpl implements ThumbnailManager {
    public static final String THUMBNAIL_EXTENSION = ".png";
    private static final String THUMBNAIL_FILE_FORMAT = "%s";

    private final SvgConverter svgConverter;
    private final StorageUploader storageUploader;
    private final String thumbnailsDirectoryName;

    public ThumbnailManagerImpl(
            final SvgConverter svgConverter,
            final StorageUploader storageUploader,
            @Value("${s3proxy.thumbnails-directory}")
            final String thumbnailsDirectoryName) {
        this.svgConverter = svgConverter;
        this.storageUploader = storageUploader;
        this.thumbnailsDirectoryName = thumbnailsDirectoryName;

    }

    public String uploadMapThumbnail(final String svgData, final Map map) {
        String fileName = makeThumbnailFileName(map);
        File pngFile = svgConverter.convertSvgToPngFile(svgData, fileName);

        String thumbnailUrl = storageUploader.upload(thumbnailsDirectoryName, pngFile);

        pngFile.delete();
        return thumbnailUrl;
    }

    public void deleteThumbnail(final Map map) {
        String fileName = makeThumbnailFileName(map);
        storageUploader.delete(thumbnailsDirectoryName, fileName + THUMBNAIL_EXTENSION);
    }

    private String makeThumbnailFileName(final Map map) {
        return String.format(THUMBNAIL_FILE_FORMAT, map.getId().toString());
    }
}
