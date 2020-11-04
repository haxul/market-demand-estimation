
package com.haxul;


import com.haxul.cacheHandler.RedisClient;
import com.haxul.exchangeCurrency.entities.CurrencyRate;
import com.haxul.exchangeCurrency.models.CurrenciesExchanges;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.clients.jedis.Jedis;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RedisClientTest {


    @Autowired
    private RedisClient redisClient;



    @Test
    public void testSerialize() {

        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setDate(new Date());
        currencyRate.setRate(80.0f);
        currencyRate.setExchangedCurrencies(CurrenciesExchanges.RUB_IN_USD);
        currencyRate.setId(10);


        redisClient.set("test", currencyRate);
        CurrencyRate cache = redisClient.get("test", CurrencyRate.class);

        assertNotNull(cache);
        assertEquals(10, cache.getId());
        assertEquals(80.0f, cache.getRate());
        assertEquals(CurrenciesExchanges.RUB_IN_USD.name(), cache.getExchangedCurrencies().name());

        CurrencyRate one = new CurrencyRate();
        one.setId(11);
        redisClient.set("test", one);
        CurrencyRate result = redisClient.get("test", CurrencyRate.class);
        assertEquals(11, result.getId());
    }
}
