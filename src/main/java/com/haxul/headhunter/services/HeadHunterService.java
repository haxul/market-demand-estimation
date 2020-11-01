package com.haxul.headhunter.services;

import com.haxul.exchangeCurrency.services.ExchangeCurrencyService;
import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.exceptions.HeadHunterSalaryIsZeroException;
import com.haxul.headhunter.exceptions.HeadHunterUnknownCurrencyException;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.models.currency.Currency;
import com.haxul.headhunter.models.experience.ExperienceHeadhunter;
import com.haxul.headhunter.models.responses.SalaryHeadHunter;
import com.haxul.headhunter.models.responses.VacancyItem;
import com.haxul.headhunter.models.responses.VacancyViewPageHeadHunter;
import com.haxul.headhunter.networkClients.HeadHunterRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public List<MarketDemand> computeMarketDemandState(String position, City city) throws InterruptedException, ExecutionException, TimeoutException {
        /*
         * get future containing general data about vacancies. There are no info about required experience in this request
         */
        var vacanciesFuture = headHunterRestClient.findVacanciesAsync(position, city.getId(), 0, new LinkedList<>());
        /*
         *  fetch info about  USD to RUB rate
         */
        Double usdToRubRate = exchangeCurrencyService.getUsdToRubRate();

        List<VacancyItem> vacanciesWithoutExperienceList = vacanciesFuture.get(10, TimeUnit.SECONDS);

        /*
            fetch experience info for each vacancy. Then transform data list to map (vacancyId -> experience)
         */
        Map<Integer, ExperienceHeadhunter> vacancyWithExperienceMap = headHunterRestClient
                .getListOfDetailedVacancies(vacanciesWithoutExperienceList)
                .get()
                .stream()
                .collect(Collectors.toMap(VacancyViewPageHeadHunter::getId, VacancyViewPageHeadHunter::getExperience));

        /*
            merge experience data with vacancy data
         */
        var vacanciesWithExperienceList = vacanciesWithoutExperienceList
                .stream()
                .peek(vacancy -> vacancy.setExperience(vacancyWithExperienceMap.get(vacancy.getId())))
                .collect(Collectors.toList());
        /*
            group vacancies by their experience
         */
        Map<ExperienceHeadhunter, List<VacancyItem>> vacanciesGroupedByExperience = vacanciesWithExperienceList
                .stream()
                .collect(Collectors.groupingBy(VacancyItem::getExperience));

        var demands = new LinkedList<MarketDemand>();

        /*
            create market demand according to vacancies data
         */
        for (var item : vacanciesGroupedByExperience.entrySet()) {
            var demand = new MarketDemand();
            demand.setPosition(position);
            demand.setAtMoment(new Date());
            demand.setAmount(item.getValue().size());
            demand.setCity(city);
            demand.setMinExperience(item.getKey().getMinYears());
            demand.setAverageGrossSalary(computeAverageGrossSalary(item.getValue(), usdToRubRate));
            demands.add(demand);

        }

        return demands;
    }

    /**
     * @param vacancies  list of vacancies
     * @param usdToRubRate USD to RUB rate
     * @return average gross rub salary of this vacancy list
     */
    private int computeAverageGrossSalary(List<VacancyItem> vacancies, double usdToRubRate) {
        int allGrossSalary = 0;
        for (var vacancy : vacancies) {
            allGrossSalary += getRubledGrossAverageSalary(vacancy, usdToRubRate);
        }
        return allGrossSalary / vacancies.size();
    }

    private int addTaxesForSalary(final int salary, final int taxRatePercentage) {
        return (salary * 100) / (100 - taxRatePercentage);
    }

    /**
       @param salary - contain info about max and min money getting in vacancy description.
       @return  depending on their existing , compute the average salary.
     */
    private int getAverageTitledSalary(final SalaryHeadHunter salary) {
        boolean doesSalaryHasFrom = salary.getFrom() != 0;
        boolean doesSalaryHasTo = salary.getTo() != 0;
        if (doesSalaryHasFrom && doesSalaryHasTo) return (salary.getFrom() + salary.getTo()) / 2;
        if (doesSalaryHasFrom) return salary.getFrom();
        if (doesSalaryHasTo) return salary.getTo();
        throw new HeadHunterSalaryIsZeroException();
    }



    /**
     * @param vacancy - containing info about salary ether USD or RUB
     * @param usdToRub - rate USD to RUB
     * @return gross RUB salary
     */

    public int getRubledGrossAverageSalary(final VacancyItem vacancy, final Double usdToRub) {
        var salary = vacancy.getSalary();
        int average = getAverageTitledSalary(salary);

        if (Currency.RUR == salary.getCurrency()) {
            return vacancy.getSalary().isGross()
                    ? average
                    : addTaxesForSalary(average, Integer.parseInt(taxRatePercentage));
        }

        if (Currency.USD == salary.getCurrency()) {
            int averageRuble = (int) (average * usdToRub);
            return salary.isGross()
                    ? averageRuble
                    : addTaxesForSalary(averageRuble, Integer.parseInt(taxRatePercentage));
        }

        throw new HeadHunterUnknownCurrencyException(vacancy.getSalary().getCurrency().toString());
    }
}
