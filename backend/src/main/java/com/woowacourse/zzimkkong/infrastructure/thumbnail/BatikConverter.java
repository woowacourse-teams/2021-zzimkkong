package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.exception.infrastructure.SvgToPngConvertException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;

@Component
@LogMethodExecutionTime(group = "infrastructure")
public class BatikConverter implements SvgConverter {
    private final PNGTranscoder pngTranscoder = new PNGTranscoder();

    @Override
    public void convertSvgToPng(InputStream inputStream, OutputStream outputStream) {
        try {
            TranscoderInput transcoderInput = new TranscoderInput(inputStream);
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);

            pngTranscoder.transcode(transcoderInput, transcoderOutput);
        } catch (TranscoderException e) {
            throw new SvgToPngConvertException(e);
        }
    }
}
