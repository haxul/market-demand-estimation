package com.haxul.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

@Configuration
public class AppConf {

    @Value("${redis.port}")
    private short port;

    @Value("${redis.host}")
    private String host;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Jedis jedisClient() {
        Jedis jedis = new Jedis("redis://" + host + ":" + port);
        jedis.auth("redis_password");
        return jedis;
    }
}
