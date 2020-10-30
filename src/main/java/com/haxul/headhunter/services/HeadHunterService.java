package com.haxul.headhunter.services;

import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.responses.VacancyItemResponse;

public interface HeadHunterService {

    MarketDemand computeMarketDemandState(String position, int areaId);

    int getRubleAverageSalary(VacancyItemResponse vacancyItemResponse, Double usdToRub);
}
