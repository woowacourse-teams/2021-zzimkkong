package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.exception.infrastructure.SvgToPngConvertException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@LogMethodExecutionTime(group = "infrastructure")
public class BatikConverter implements SvgConverter {
    private final String saveDirectoryPath;

    public BatikConverter(
            @Value("${converter.temp.location}")
                    String saveDirectoryPath) {
        this.saveDirectoryPath = saveDirectoryPath;
    }

    public File convertSvgToPngFile(final String mapSvgData, final String fileName) {
        try {
            ByteArrayInputStream svgInput = new ByteArrayInputStream(mapSvgData.getBytes());
            TranscoderInput transcoderInput = new TranscoderInput(svgInput);

            String targetFileName = saveDirectoryPath + fileName + ".png";
            OutputStream outputStream = new FileOutputStream(targetFileName);
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);

            PNGTranscoder pngTranscoder = new PNGTranscoder();
            pngTranscoder.transcode(transcoderInput, transcoderOutput);

            outputStream.flush();
            outputStream.close();

            return new File(targetFileName);
        } catch (IOException | TranscoderException e) {
            throw new SvgToPngConvertException(e);
        }
    }

    public String getSaveDirectoryPath() {
        return saveDirectoryPath;
    }

    @Override
    public void convertSvgToPng(InputStream inputStream, OutputStream outputStream) {
        try {
            TranscoderInput transcoderInput = new TranscoderInput(inputStream);
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
            PNGTranscoder pngTranscoder = new PNGTranscoder();

            pngTranscoder.transcode(transcoderInput, transcoderOutput);
        } catch (TranscoderException e) {
            throw new SvgToPngConvertException(e);
        }
    }
}
