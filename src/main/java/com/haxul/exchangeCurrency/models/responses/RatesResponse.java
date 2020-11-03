package com.haxul.exchangeCurrency.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RatesResponse {
    @JsonProperty("RUB")
    private Float rub;

    @JsonProperty("USD")
    private Float usd;
}
