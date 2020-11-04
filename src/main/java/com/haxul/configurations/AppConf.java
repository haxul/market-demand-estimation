package com.haxul.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

@Configuration
public class AppConf {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Jedis jedisClient() {
        Jedis jedis = new Jedis("redis://localhost:6379");
        jedis.auth("redis_password");
        return jedis;
    }
}
