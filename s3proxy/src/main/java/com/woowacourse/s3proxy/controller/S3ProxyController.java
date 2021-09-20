package com.woowacourse.s3proxy.controller;

import com.woowacourse.s3proxy.service.AmazonS3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api")
public class S3ProxyController {
    private final AmazonS3Service amazonS3Service;

    public S3ProxyController(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    @RequestMapping(value = "/storage/{directoryPath}", method = RequestMethod.POST)
    public ResponseEntity<Void> submit(@RequestParam("file") MultipartFile file, @PathVariable("directoryPath") String directoryPath) {
        URI location = amazonS3Service.upload(file, directoryPath);
        return ResponseEntity.created(location).build();
    }
}
