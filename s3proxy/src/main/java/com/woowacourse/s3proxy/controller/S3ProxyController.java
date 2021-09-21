package com.woowacourse.s3proxy.controller;

import com.woowacourse.s3proxy.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/storage")
public class S3ProxyController {
    private final S3Service s3Service;

    public S3ProxyController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/{directoryPath}")
    public ResponseEntity<Void> submit(
            @RequestParam("file") MultipartFile file,
            @PathVariable("directoryPath") String directoryPath) {
        URI location = s3Service.upload(file, directoryPath);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{directoryPath}/{fileName}")
    public ResponseEntity<Void> delete(
            @PathVariable("directoryPath") String directoryPath,
            @PathVariable("fileName") String fileName) {
        s3Service.delete(directoryPath, fileName);
        return ResponseEntity.noContent().build();
    }
}
