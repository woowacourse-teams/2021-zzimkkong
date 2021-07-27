package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.SvgDto;
import com.woowacourse.zzimkkong.infrastructure.S3Uploader;
import com.woowacourse.zzimkkong.infrastructure.SvgConverter;
import com.woowacourse.zzimkkong.service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class PracticeController {
    private SvgConverter svgConverter;
    private S3Uploader s3Uploader;

    public PracticeController(SvgConverter svgConverter, S3Uploader s3Uploader) {
        this.svgConverter = svgConverter;
        this.s3Uploader = s3Uploader;
    }

    @PostMapping("/practice")
    public ResponseEntity<Void> create(@RequestBody SvgDto svgDto) {
        File file = svgConverter.convertSvgToPng(svgDto.getName(), svgDto.getSvg());
        String url = s3Uploader.upload("test/", file);
        return ResponseEntity.created(URI.create(url)).build();
    }
}
