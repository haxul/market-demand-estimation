package com.haxul.headhunter.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.haxul.headhunter.models.currency.Currency;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalaryHeadHunter {
    private int from;
    private int to;
    private Currency currency;
    private boolean gross;
}
