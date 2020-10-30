package com.haxul.exchangeCurrency;


import com.haxul.exchangeCurrency.models.responses.CurrencyResponse;
import com.haxul.exchangeCurrency.networkClients.ExchangeRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ExchangeRestClientTest {

    @Autowired
    private ExchangeRestClient exchangeRestClient;


    @Test
    public void fetchCurrencies() throws ExecutionException, InterruptedException {
        CurrencyResponse currencyResponse = exchangeRestClient.fetchCurrencies().get();
        assertNotNull(currencyResponse);
        assertEquals("EUR", currencyResponse.getBase());
    }

}
