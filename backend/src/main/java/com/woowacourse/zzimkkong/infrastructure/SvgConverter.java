package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.exception.infrastructure.SvgToPngConvertException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class SvgConverter {
    public static final String SAVE_DIRECTORY_PATH = "src/main/resources/tmp/";

    public File convertSvgToPngFile(final String mapSvgData, final String fileName) {
        try {
            ByteArrayInputStream svgInput = new ByteArrayInputStream(mapSvgData.getBytes());
            TranscoderInput transcoderInput = new TranscoderInput(svgInput);

            String targetFileName = SAVE_DIRECTORY_PATH + fileName + ".png";
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
}
