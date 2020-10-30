package com.haxul.exchangeCurrency.networkClients;

import com.haxul.exchangeCurrency.exceptions.ExchangeHttpException;
import com.haxul.exchangeCurrency.models.responses.CurrencyResponse;
import com.haxul.exchangeCurrency.models.responses.RatesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ExchangeRestClient {

    @Value("${currency.api.url}")
    private String url;

    private final RestTemplate restTemplate;

    public ExchangeRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CompletableFuture<CurrencyResponse> fetchCurrencies() {
        try {
            return CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url, CurrencyResponse.class));
        } catch (Exception e) {
            log.error("Exchange restClient error: " + e);
            throw new ExchangeHttpException(e);
        }
    }

}
