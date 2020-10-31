package com.haxul.headhunter.services;

import com.haxul.exchangeCurrency.services.ExchangeCurrencyService;
import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.exceptions.HeadHunterSalaryIsZeroException;
import com.haxul.headhunter.exceptions.HeadHunterUnknownCurrencyException;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.models.currency.Currency;
import com.haxul.headhunter.models.responses.SalaryVacancyResponse;
import com.haxul.headhunter.models.responses.VacancyItemResponse;
import com.haxul.headhunter.networkClients.HeadHunterRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class HeadHunterService {

    private final HeadHunterRestClient headHunterRestClient;
    private final ExchangeCurrencyService exchangeCurrencyService;

    @Value("${headhunter.taxRatePercentage}")
    private String taxRatePercentage;

    public HeadHunterService(HeadHunterRestClient headHunterRestClient, ExchangeCurrencyService exchangeCurrencyService) {
        this.headHunterRestClient = headHunterRestClient;
        this.exchangeCurrencyService = exchangeCurrencyService;
    }

    public MarketDemand computeMarketDemandState(String position, City city) throws InterruptedException, ExecutionException, TimeoutException {
        var vacanciesFuture = headHunterRestClient.findVacanciesAsync(position, city.getId(), 0, new LinkedList<>());

        MarketDemand demand = new MarketDemand();
        demand.setPosition(position);
        demand.setCity(city);

        Double usdToRubRate = exchangeCurrencyService.getUsdToRubRate();
        List<VacancyItemResponse> vacancies = vacanciesFuture.get(10, TimeUnit.SECONDS);
        demand.setAmount(vacancies.size());

        int allGrossSalary = 0;
        for (var vacancy : vacancies) {
            allGrossSalary += getRubleAverageSalary(vacancy, usdToRubRate);
        }

        int averageGrossSalary = allGrossSalary / vacancies.size();
        demand.setAverageGrossSalary(averageGrossSalary);
        demand.setAtMoment(new Date());

        return demand;
    }

    private int computeGrossSalary(final int salary, final int taxRatePercentage) {
        return (salary * 100) / (100 - taxRatePercentage);
    }

    private int computeAverage(final SalaryVacancyResponse salary) {
        boolean doesSalaryHasFrom = salary.getFrom() != 0;
        boolean doesSalaryHasTo = salary.getTo() != 0;
        if (doesSalaryHasFrom && doesSalaryHasTo) return (salary.getFrom() + salary.getTo()) / 2;
        if (doesSalaryHasFrom) return salary.getFrom();
        if (doesSalaryHasTo) return salary.getTo();
        throw new HeadHunterSalaryIsZeroException();
    }


    public int getRubleAverageSalary(final VacancyItemResponse vacancy, final Double usdToRub) {
        var salary = vacancy.getSalary();
        int average = computeAverage(salary);

        if (Currency.RUR == salary.getCurrency()) {
            return vacancy.getSalary().isGross()
                    ? average
                    : computeGrossSalary(average, Integer.parseInt(taxRatePercentage));
        }

        if (Currency.USD == salary.getCurrency()) {
            int averageRuble = (int) (average * usdToRub);
            return salary.isGross()
                    ? averageRuble
                    : computeGrossSalary(averageRuble, Integer.parseInt(taxRatePercentage));
        }

        throw new HeadHunterUnknownCurrencyException(vacancy.getSalary().getCurrency().toString());
    }

}
