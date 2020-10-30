package com.haxul.headhunter.models.responses;

import lombok.Data;

@Data
public class SalaryVacancyResponse {
    private int from;
    private int to;
    private String currency;
    private boolean gross;
}
