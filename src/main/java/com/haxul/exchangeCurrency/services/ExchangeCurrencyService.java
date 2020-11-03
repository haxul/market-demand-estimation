package com.haxul.exchangeCurrency.services;

import com.haxul.exchangeCurrency.entities.CurrencyRate;
import com.haxul.exchangeCurrency.models.CurrenciesExchanges;
import com.haxul.exchangeCurrency.models.responses.CurrencyResponse;
import com.haxul.exchangeCurrency.models.responses.RatesResponse;
import com.haxul.exchangeCurrency.networkClients.ExchangeRestClient;
import com.haxul.exchangeCurrency.repositories.ExchangeCurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.haxul.exchangeCurrency.models.CurrenciesExchanges.RUB_IN_USD;

@Service
@RequiredArgsConstructor
public class ExchangeCurrencyService {

    private final ExchangeRestClient exchangeRestClient;
    private final ExchangeCurrencyRepository exchangeCurrencyRepository;


    public Float getUsdToRubRate() throws ExecutionException, InterruptedException, TimeoutException {
        return exchangeRestClient
                .fetchCurrencies()
                .thenApply(this::computeUsdToRub)
                .get(5, TimeUnit.SECONDS);
    }

    private Float computeUsdToRub(CurrencyResponse currencyResponse) {
        RatesResponse rates = currencyResponse.getRates();
        return rates.getRub()/ rates.getUsd();
    }



    public CurrencyRate findCurrencyRateByExchangedCurrenciesAndDate(CurrenciesExchanges exchangedCurrencies, Date date) {
        return exchangeCurrencyRepository.findByExchangedCurrenciesAndDate(exchangedCurrencies, date);
    }

    public CurrencyRate findByExchangedCurrencies(CurrenciesExchanges exchanges) {
        return exchangeCurrencyRepository.findByExchangedCurrencies(exchanges);
    }

    public void createCurrencyRate(Float rate, CurrenciesExchanges currenciesExchanges) {
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setDate(new Date());
        currencyRate.setExchangedCurrencies(currenciesExchanges);
        currencyRate.setRate(rate);
        exchangeCurrencyRepository.save(currencyRate);
    }

    public void updateCurrencyRate(CurrencyRate currencyRate, float rate) {
        exchangeCurrencyRepository.updateRateForCurrencyRate(currencyRate.getId(), rate, new Date());
    }
}
