package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:config/slack.properties")
public class SlackConfig implements WebMvcConfigurer {
    @Bean
    @Profile("!test")
    public SlackUrl slackUrl(
            @Value("${slack.webhook.local}") final String devUrl) {
        return new SlackUrl(devUrl);
    }

    @Bean
    @Profile("test")
    public SlackUrl slackUrlTest(
            @Value("${slack.webhook.test}") final String testUrl) {
        return new SlackUrl(testUrl);
    }
}
