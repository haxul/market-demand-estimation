package com.haxul.exchangeCurrency.repositories;

import com.haxul.exchangeCurrency.entities.CurrencyRate;
import com.haxul.exchangeCurrency.models.CurrenciesExchanges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ExchangeCurrencyRepository extends JpaRepository<CurrencyRate, Integer> {

    CurrencyRate findByExchangedCurrenciesAndDate(CurrenciesExchanges exchangedCurrencies, Date date);
    CurrencyRate findByExchangedCurrencies(CurrenciesExchanges exchanges);

    @Modifying
    @Query("update CurrencyRate cr set cr.rate = :rate, cr.date =:date where cr.id = :id")
    void updateRateForCurrencyRate(@Param("id") int id, @Param("rate") float rate, @Param("date") Date date);
}
