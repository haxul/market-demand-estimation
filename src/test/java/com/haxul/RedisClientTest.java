
package com.haxul;


import com.haxul.cacheClients.RedisClient;
import com.haxul.exchangeCurrency.entities.CurrencyRate;
import com.haxul.exchangeCurrency.models.CurrenciesExchanges;
import com.haxul.headhunter.entities.MarketDemand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RedisClientTest {


    @Autowired
    private RedisClient redisClient;


    @Test
    public void testSerializeList() {
        var one = new MarketDemand();
        one.setId(1L);

        var two = new MarketDemand();
        two.setId(2L);

        List<MarketDemand> list = new LinkedList<>();
        list.add(one);
        list.add(two);

        redisClient.set("list", list);
        List<MarketDemand> result = redisClient.getList("list", MarketDemand.class);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

    }

    @Test
    public void testFlushAll() {
        var one = new MarketDemand();
        one.setId(1L);

        var two = new MarketDemand();
        two.setId(2L);

        List<MarketDemand> list = new LinkedList<>();
        list.add(one);
        list.add(two);

        redisClient.set("demands:10-10-10", list);

        var three = new MarketDemand();
        three.setId(100L);

        List<MarketDemand> anotherList = new LinkedList<>();
        anotherList.add(three);
        redisClient.set("test", anotherList);

        redisClient.clearByPattern("demands:*");

        List<MarketDemand> result = redisClient.getList("demands:10-10-10", MarketDemand.class);
        List<MarketDemand> anotherResult = redisClient.getList("test", MarketDemand.class);

        assertNull(result);
        assertNotNull(anotherResult);
        assertEquals(100L, anotherResult.get(0).getId());
    }

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
