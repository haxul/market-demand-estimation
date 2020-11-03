package com.haxul.headhunter.services;

import com.haxul.exchangeCurrency.services.ExchangeCurrencyService;
import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.exceptions.HeadHunterSalaryIsZeroException;
import com.haxul.headhunter.exceptions.HeadHunterUnknownCurrencyException;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.models.currency.Currency;
import com.haxul.headhunter.models.experience.ExperienceHeadhunter;
import com.haxul.headhunter.models.hhApiResponses.SalaryHeadHunter;
import com.haxul.headhunter.models.hhApiResponses.VacancyDetailedPageHeadHunter;
import com.haxul.headhunter.models.hhApiResponses.VacancyHeadHunter;
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

    /**
     * @param position - name of vacancy looked vor in HeadHunter
     * @param city     - city name where vacancy is placed
     * @return list of market demands grouped by experience valued in years. Values are relevant for now
     */

    public List<MarketDemand> findMarketDemandsForToday(String position, City city, String source) throws InterruptedException, ExecutionException, TimeoutException {
        /*
         * get future containing general data about vacancies. There are no info about required experience in this request
         */
        var vacanciesFuture = headHunterRestClient.findVacanciesAsync(position, city.getId(), 0, new LinkedList<>());
        /*
         *  fetch info about  USD to RUB rate
         */
        Double usdToRubRate = exchangeCurrencyService.getUsdToRubRate();

        /*
            complete vacancies future and filter vacancies which have salary equaled null
         */

        List<VacancyHeadHunter> vacanciesWithoutExperienceList = vacanciesFuture
                .get()
                .stream()
                .filter(vacancy -> vacancy.getSalary() != null)
                .collect(Collectors.toList());


        /*
            fetch experience info for each vacancy.
         */


        var notUniqueDetailedVacancyList  = headHunterRestClient
                .getListOfDetailedVacancies(vacanciesWithoutExperienceList)
                .get(60, TimeUnit.SECONDS);

        /*
            make list be unique. We are doing because  sometimes Headhunter Api returns vacancies with the same id
         */
            
        Set<VacancyDetailedPageHeadHunter> uniqueDetailedVacancies = new HashSet<>(notUniqueDetailedVacancyList);

        /*
            Then transform data list to map (vacancyId -> experience)
         */

        Map<Integer, ExperienceHeadhunter> vacancyWithExperienceMap = uniqueDetailedVacancies
                .stream()
                .collect(Collectors.toMap(VacancyDetailedPageHeadHunter::getId, VacancyDetailedPageHeadHunter::getExperience));

        /*
            merge experience data with vacancy data
         */
        var vacanciesWithExperienceList = vacanciesWithoutExperienceList
                .stream()
                .peek(vacancy -> vacancy.setExperience(vacancyWithExperienceMap.get(vacancy.getId())))
                .collect(Collectors.toList());

        /*
            if list is empty it is no sense to continue info collecting
         */

        if (vacanciesWithoutExperienceList.isEmpty()) return new LinkedList<>();

        /*
            group vacancies by their experience
         */
        Map<ExperienceHeadhunter, List<VacancyHeadHunter>> vacanciesGroupedByExperience = vacanciesWithExperienceList
                .stream()
                .collect(Collectors.groupingBy(VacancyHeadHunter::getExperience));

        var demands = new LinkedList<MarketDemand>();

        /*
            create market demand according to vacancies data
         */
        for (var experienceVacancyListEntry : vacanciesGroupedByExperience.entrySet()) {
            var demand = new MarketDemand();
            demand.setPosition(position);
            demand.setAtMoment(new Date());
            demand.setAmount(experienceVacancyListEntry.getValue().size());
            demand.setCity(city);
            demand.setMinYearExperience(experienceVacancyListEntry.getKey().getMinYears());
            var vacanciesWithDefinedCurrencyType = experienceVacancyListEntry.getValue()
                    .stream()
                    .filter(vacancy -> vacancy.getSalary().getCurrency() != null)
                    .collect(Collectors.toList());
            int averageRubGrossSalary = computeAverageRubledGrossSalaryForVacancyList(vacanciesWithDefinedCurrencyType, usdToRubRate);
            demand.setAverageRubGrossSalary(averageRubGrossSalary);
            demand.setSource(source);
            demands.add(demand);
        }
        demands.sort((a, b) -> Integer.compare(b.getMinYearExperience(), a.getMinYearExperience()));
        return demands;
    }

    /**
     * @param vacancies    list of vacancies
     * @param usdToRubRate USD to RUB rate
     * @return average gross rub salary of this vacancy list
     */
    private int computeAverageRubledGrossSalaryForVacancyList(List<VacancyHeadHunter> vacancies, double usdToRubRate) {
        int allGrossSalary = 0;
        for (var vacancy : vacancies) {
            allGrossSalary += getRubledGrossAverageSalaryForVacancy(vacancy, usdToRubRate);
        }
        return allGrossSalary / vacancies.size();
    }

    private int addTaxesForSalary(final int salary, final int taxRatePercentage) {
        return (salary * 100) / (100 - taxRatePercentage);
    }

    /**
     * @param salary - contain info about max and min money getting in vacancy description.
     * @return depending on their existing , compute the average salary.
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
     * @param vacancy  - containing info about one salary ether USD or RUB
     * @param usdToRub - rate USD to RUB
     * @return gross RUB salary
     */

    public int getRubledGrossAverageSalaryForVacancy(final VacancyHeadHunter vacancy, final Double usdToRub) {
        var salary = vacancy.getSalary();
        int average = getAverageTitledSalary(salary);

        if (Currency.RUR == salary.getCurrency()) return addTaxesIfItIsNotGross(salary.isGross(), average);
        else if (Currency.USD == salary.getCurrency()) {
            int averageRuble = (int) (average * usdToRub);
            return addTaxesIfItIsNotGross(salary.isGross(), averageRuble);
        }

        throw new HeadHunterUnknownCurrencyException(vacancy.getSalary().getCurrency().toString());
    }

    private int addTaxesIfItIsNotGross(boolean isGross, int salary) {
        return isGross ? salary : addTaxesForSalary(salary, Integer.parseInt(taxRatePercentage));
    }
}
