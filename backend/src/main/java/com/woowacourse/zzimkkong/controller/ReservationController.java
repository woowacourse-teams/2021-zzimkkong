package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.reservation.ReservationFindAllResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationFindResponse;
import com.woowacourse.zzimkkong.service.ReservationService;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.time.LocalDate;

import static com.woowacourse.zzimkkong.dto.Validator.DATE_FORMAT;

public abstract class ReservationController<Service extends ReservationService> {
    protected final Service reservationService;

    protected ReservationController(final Service reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<ReservationFindAllResponse> findAll(
            @PathVariable final Long mapId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindAllResponse reservationFindAllResponse = reservationService.findAllReservations(mapId, date);
        return ResponseEntity.ok().body(reservationFindAllResponse);
    }

    @GetMapping("/spaces/{spaceId}/reservations")
    public ResponseEntity<ReservationFindResponse> find(
            @PathVariable final Long mapId,
            @PathVariable final Long spaceId,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDate date) {
        ReservationFindResponse reservationFindResponse = reservationService.findReservations(mapId, spaceId, date);
        return ResponseEntity.ok().body(reservationFindResponse);
    }

    @GetMapping("/practice")
    public ResponseEntity<Void> practice() throws IOException, TranscoderException {

        String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\" id=\"Layer_1\" x=\"0px\" y=\"0px\" width=\"200px\" height=\"200px\" viewBox=\"0 0 200 200\" enable-background=\"new 0 0 200 200\" xml:space=\"preserve\">\n" +
                "<g>\n" +
                "\t<polygon fill=\"#FFFFFF\" points=\"100,174.127 36.593,137.896 100,106.914 163.407,137.896  \"/>\n" +
                "\t<path d=\"M100,17.956L25.625,52.717v86.808L100,182.044l74.375-42.521V52.717L100,17.956z M32.625,57.172L96.5,27.318v73.369   l-63.875,31.212V57.172z M100,173.981l-63.407-36.23L100,106.769l63.407,30.982L100,173.981z M167.375,131.899L103.5,100.688   V27.318l63.875,29.854V131.899z\"/>\n" +
                "\t<polygon fill=\"#EEEEEE\" points=\"32.625,57.172 96.5,27.318 96.5,100.688 32.625,131.899  \"/>\n" +
                "\t<polygon fill=\"#EEEEEE\" points=\"167.375,131.899 103.5,100.688 103.5,27.318 167.375,57.172  \"/>\n" +
                "\t<g>\n" +
                "\t\t<polygon fill=\"#F47522\" points=\"52.079,103.708 65.514,90.273 83.563,108.321 137.236,54.648 150.67,68.083 83.563,135.192   \"/>\n" +
                "\t\t<path d=\"M137.236,60.305l7.777,7.778l-61.451,61.452l-25.827-25.827l7.778-7.778l18.048,18.048L137.236,60.305 M137.236,48.991    l-5.657,5.657l-48.017,48.017L71.171,90.273l-5.657-5.657l-5.657,5.656l-7.778,7.778l-5.657,5.657l5.657,5.657l25.827,25.827    l5.657,5.656l5.657-5.656l61.452-61.452l5.656-5.656l-5.656-5.657l-7.777-7.779L137.236,48.991L137.236,48.991z\"/>\n" +
                "\t</g>\n" +
                "\t\n" +
                "\t\t<radialGradient id=\"SVGID_1_\" cx=\"100.8877\" cy=\"139.3516\" r=\"33.9239\" gradientTransform=\"matrix(1.0978 0 0 0.269 -10.0625 103.0377)\" gradientUnits=\"userSpaceOnUse\">\n" +
                "\t\t<stop offset=\"0\" style=\"stop-color:#000000;stop-opacity:0.4\"/>\n" +
                "\t\t<stop offset=\"1\" style=\"stop-color:#000000;stop-opacity:0\"/>\n" +
                "\t</radialGradient>\n" +
                "\t<path opacity=\"0.6\" fill=\"url(#SVGID_1_)\" d=\"M152.625,139.503c0,12.368-23.253,22.4-51.938,22.4   c-28.684,0-51.938-10.032-51.938-22.4c0-9.906,23.253-20.364,51.938-20.364C129.372,119.139,152.625,129.343,152.625,139.503z\"/>\n" +
                "</g>\n" +
                "</svg>";

        ByteArrayInputStream svgInput = new ByteArrayInputStream(svg.getBytes());
        TranscoderInput transcoderInput = new TranscoderInput(svgInput);

        OutputStream outputStream = new FileOutputStream("/Users/yeonwoocho/Downloads/zzkk.png");
        TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);

        PNGTranscoder pngTranscoder = new PNGTranscoder();
        pngTranscoder.transcode(transcoderInput, transcoderOutput);


        outputStream.flush();
        outputStream.close();

        return ResponseEntity.ok().build();
    }
}
