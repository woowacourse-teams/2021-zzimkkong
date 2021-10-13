package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.exception.infrastructure.CannotDeleteConvertedFileException;
import com.woowacourse.zzimkkong.exception.infrastructure.CannotGenerateInputStreamFromSvgDataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

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
            @Value("${s3proxy.thumbnails-directory}") final String thumbnailsDirectoryName) {
        this.svgConverter = svgConverter;
        this.storageUploader = storageUploader;
        this.thumbnailsDirectoryName = thumbnailsDirectoryName;

    }

    public String uploadMapThumbnail(final String svgData, final Map map) {
        String fileName = makeThumbnailFileName(map);
        File pngFile = svgConverter.convertSvgToPngFile(svgData, fileName);

        String thumbnailUrl = storageUploader.upload(thumbnailsDirectoryName, pngFile);

        if (!pngFile.delete()) {
            throw new CannotDeleteConvertedFileException();
        }
        return thumbnailUrl;
    }

    public String uploadMapThumbnailInMemory(final String svgData, final Map map) {
        try (final ByteArrayInputStream svgInputStream = new ByteArrayInputStream(svgData.getBytes());
             final ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            final BufferedInputStream bufferedSvgInputStream = new BufferedInputStream(svgInputStream);
            final BufferedOutputStream bufferedPngOutputStream = new BufferedOutputStream(pngOutputStream);

            final String fileName = makeThumbnailFileNameWithExtension(map);
            svgConverter.convertSvgToPng(bufferedSvgInputStream, bufferedPngOutputStream);

            return uploadFromByteArray(fileName, pngOutputStream.toByteArray());
        } catch (IOException exception) {
            throw new CannotGenerateInputStreamFromSvgDataException(exception);
        }
    }

    private String makeThumbnailFileName(final Map map) {
        return String.format(THUMBNAIL_FILE_FORMAT, map.getId().toString());
    }

    private String makeThumbnailFileNameWithExtension(final Map map) {
        return String.format(THUMBNAIL_FILE_FORMAT, map.getId().toString() + THUMBNAIL_EXTENSION);
    }

    private String uploadFromByteArray(String fileName, byte[] byteArray) throws IOException {
        try (final BufferedInputStream bufferedPngInputStream = new BufferedInputStream(new ByteArrayInputStream(byteArray))) {
            return storageUploader.upload(thumbnailsDirectoryName, fileName, bufferedPngInputStream);
        }
    }

    public void deleteThumbnail(final Map map) {
        String fileName = makeThumbnailFileName(map);
        storageUploader.delete(thumbnailsDirectoryName, fileName + THUMBNAIL_EXTENSION);
    }
}
