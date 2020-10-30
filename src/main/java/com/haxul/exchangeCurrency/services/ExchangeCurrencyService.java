package com.haxul.exchangeCurrency.services;

import com.haxul.exchangeCurrency.models.responses.CurrencyResponse;
import com.haxul.exchangeCurrency.models.responses.RatesResponse;
import com.haxul.exchangeCurrency.networkClients.ExchangeRestClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class ExchangeCurrencyService {

    private final ExchangeRestClient exchangeRestClient;

    public ExchangeCurrencyService(ExchangeRestClient exchangeRestClient) {
        this.exchangeRestClient = exchangeRestClient;
    }

    public Double getUsdToRubRate() throws ExecutionException, InterruptedException, TimeoutException {
        return exchangeRestClient
                .fetchCurrencies()
                .thenApply(this::computeUsdToRub)
                .get(5, TimeUnit.SECONDS);
    }

    private Double computeUsdToRub(CurrencyResponse currencyResponse) {
        RatesResponse rates = currencyResponse.getRates();
        return rates.getRub()/ rates.getUsd();
    }
}
