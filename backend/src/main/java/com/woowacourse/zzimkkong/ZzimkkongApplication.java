package com.woowacourse.zzimkkong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class ZzimkkongApplication {
    @PostConstruct
    private void setTimeZone(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        // 젠킨스 촉발
        // 젠킨스 촉발2
        SpringApplication.run(ZzimkkongApplication.class, args);
    }
}
