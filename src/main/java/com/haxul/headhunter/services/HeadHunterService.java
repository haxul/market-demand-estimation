package com.haxul.headhunter.services;

import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.exceptions.HeadHunterSalaryIsZeroException;
import com.haxul.headhunter.exceptions.HeadHunterUnknownCurrencyException;
import com.haxul.headhunter.exceptions.HeadHunterWrongResponseException;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.models.responses.SalaryVacancyResponse;
import com.haxul.headhunter.models.responses.VacanciesResponse;
import com.haxul.headhunter.models.responses.VacancyItemResponse;
import com.haxul.headhunter.networkClients.HeadHunterRestClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HeadHunterService {


    private final HeadHunterRestClient headHunterRestClient;

    @Value("${headhunter.taxRatePercentage}")
    private String taxRatePercentage;

    private final double USD_TO_RUB_RATE = 79.12; // TODO fetch rate from the network

    public HeadHunterService(HeadHunterRestClient headHunterRestClient) {
        this.headHunterRestClient = headHunterRestClient;
    }

    @SneakyThrows
    public MarketDemand computeMarketDemandState(String position, City city) {
        var vacanciesFuture = CompletableFuture.supplyAsync(() ->
                        headHunterRestClient.findVacancies(position, city.getId(), 0, new LinkedList<>()));

        MarketDemand demand = new MarketDemand();
        demand.setPosition(position);
        demand.setCity(city);

        List<VacancyItemResponse> vacancies = vacanciesFuture.get(7, TimeUnit.SECONDS);
        demand.setAmount(vacancies.size());

        List<VacancyItemResponse> vacanciesWithSalary = vacancies.stream()
                .filter(item -> item.getSalary() != null)
                .collect(Collectors.toList());

        int allGrossSalary = 0;
        for (var vacancy : vacanciesWithSalary) {
            allGrossSalary += getRubleAverageSalary(vacancy, USD_TO_RUB_RATE);
        }
        int averageGrossSalary = allGrossSalary / vacanciesWithSalary.size();
        demand.setAverageGrossSalary(averageGrossSalary);
        demand.setAtMoment(new Date());

        return demand;
    }

    private int computeGrossSalary(final int salary,final int taxRatePercentage) {
        return (salary * 100) / (100 - taxRatePercentage);
    }

    private int computeAverage(final SalaryVacancyResponse salary) {
        boolean doesSalaryHasFrom =  salary.getFrom() != 0;
        boolean doesSalaryHasTo = salary.getTo() != 0;
        if (doesSalaryHasFrom && doesSalaryHasTo) return  (salary.getFrom() + salary.getTo()) / 2;
        if (doesSalaryHasFrom) return salary.getFrom();
        if (doesSalaryHasTo) return salary.getTo();
        throw new HeadHunterSalaryIsZeroException();
    }


    public int getRubleAverageSalary(final VacancyItemResponse vacancy, final Double usdToRub) {
        var salary = vacancy.getSalary();
        int average = computeAverage(salary);

        if (salary.getCurrency().equals("RUR")) {
            return vacancy.getSalary().isGross()
                    ? average
                    : computeGrossSalary(average, Integer.parseInt(taxRatePercentage));
        }

        if (salary.getCurrency().equals("USD")) {
            int averageRuble = (int) (average * usdToRub);
            return salary.isGross()
                    ? averageRuble
                    : computeGrossSalary(averageRuble, Integer.parseInt(taxRatePercentage));
        }

        throw new HeadHunterUnknownCurrencyException(vacancy.getSalary().getCurrency());
    }



}
