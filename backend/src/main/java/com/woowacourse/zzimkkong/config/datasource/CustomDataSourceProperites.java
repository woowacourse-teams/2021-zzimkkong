package com.woowacourse.zzimkkong.config.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "datasource")
public class CustomDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private final Map<String, Slave> slave = new HashMap<>();

    @Getter
    @Setter
    public static class Slave {
        private String name;
        private String url;
    }
}
