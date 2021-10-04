package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.woowacourse.zzimkkong.domain.Map;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ThumbnailManager {
    public static final String THUMBNAILS_DIRECTORY_NAME = "thumbnails";
    public static final String THUMBNAIL_EXTENSION = ".png";
    private static final String THUMBNAIL_FILE_FORMAT = "%s";

    private final SvgConverter svgConverter;
    private final StorageUploader storageUploader;

    public ThumbnailManager(final SvgConverter svgConverter, final StorageUploader storageUploader) {
        this.svgConverter = svgConverter;
        this.storageUploader = storageUploader;
    }

    public String uploadMapThumbnail(final String svgData, final Map map) {
        String fileName = makeThumbnailFileName(map);
        File pngFile = svgConverter.convertSvgToPngFile(svgData, fileName);

        String thumbnailUrl = storageUploader.upload(THUMBNAILS_DIRECTORY_NAME, pngFile);

        pngFile.delete();
        return thumbnailUrl;
    }

    public void deleteThumbnail(final Map map) {
        String fileName = makeThumbnailFileName(map);
        storageUploader.delete(THUMBNAILS_DIRECTORY_NAME, fileName + THUMBNAIL_EXTENSION);
    }

    private String makeThumbnailFileName(final Map map) {
        return String.format(THUMBNAIL_FILE_FORMAT, map.getId().toString());
    }
}
