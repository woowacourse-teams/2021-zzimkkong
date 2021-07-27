package com.woowacourse.zzimkkong.infrastructure;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

@Component
public class SvgConverter {

    public File convertSvgToPng(final String fileName, final String mapSvgData) {
        try {
//            String tmpFileName = UUID.randomUUID().toString();
            String tempFileName = "src/main/resources/tmp/" + fileName + ".png";

            ByteArrayInputStream svgInput = new ByteArrayInputStream(mapSvgData.getBytes());
            TranscoderInput transcoderInput = new TranscoderInput(svgInput);

            OutputStream outputStream = new FileOutputStream(tempFileName);
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);

            PNGTranscoder pngTranscoder = new PNGTranscoder();
            pngTranscoder.transcode(transcoderInput, transcoderOutput);

            outputStream.flush();
            outputStream.close();

            return new File(tempFileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
