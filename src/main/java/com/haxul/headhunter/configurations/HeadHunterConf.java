package com.haxul.headhunter.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HeadHunterConf {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
