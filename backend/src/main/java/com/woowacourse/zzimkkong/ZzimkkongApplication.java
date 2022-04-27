package com.woowacourse.zzimkkong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@SpringBootApplication
public class ZzimkkongApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(UTC);
        SpringApplication.run(ZzimkkongApplication.class, args);
    }
}
