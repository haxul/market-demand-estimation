package com.haxul.exchangeCurrency.models.responses;

import lombok.Data;

import java.util.Date;

@Data
public class CurrencyResponse {
    private RatesResponse rates;
    private String base;
    private Date date;
}
