package com.haxul.headhunter;


import com.haxul.exchangeCurrency.services.ExchangeCurrencyService;
import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.models.currency.Currency;
import com.haxul.headhunter.models.experience.ExperienceHeadhunter;
import com.haxul.headhunter.models.hhApiResponses.SalaryHeadHunter;
import com.haxul.headhunter.models.hhApiResponses.VacancyDetailedPageHeadHunter;
import com.haxul.headhunter.models.hhApiResponses.VacancyHeadHunter;
import com.haxul.headhunter.networkClients.HeadHunterRestClient;
import com.haxul.headhunter.services.HeadHunterService;
import com.haxul.utils.AppUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class HeadHunterServiceTest {

    @Value("${headhunter.baseUrl}")
    private String baseUrl;

    @Autowired
    private HeadHunterService headHunterService;

    @MockBean
    private HeadHunterRestClient headHunterRestClient;

    @MockBean
    private ExchangeCurrencyService exchangeCurrencyService;

    @BeforeEach
    public void executedBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void findMarketDemandsForTodayNetworkTest() throws InterruptedException, ExecutionException, TimeoutException {
        var restClient = new HeadHunterRestClient(new RestTemplate(), new AppUtils());
        restClient.setBaseUrl(baseUrl);
        List<VacancyHeadHunter> list = restClient.findVacanciesAsync("java", City.SAMARA.getId(), 0, new LinkedList<>()).get(5, TimeUnit.SECONDS);
        assertNotEquals(0, list.size());
    }

    @Test
    public void findMarketDemandsForTodayTest() throws InterruptedException, ExecutionException, TimeoutException {
        var vacancies = List.of(0, 1, 2, 3, 4, 5).stream().map(i -> {
            var vacancy = new VacancyHeadHunter();
            vacancy.setId(i);
            return vacancy;
        }).collect(Collectors.toList());
        // MIN_1_MAX3 ==============================
        var salary0 = new SalaryHeadHunter();
        salary0.setCurrency(Currency.RUR);
        salary0.setGross(true);
        salary0.setFrom(70000);
        salary0.setTo(80000); // 75 000
        vacancies.get(0).setSalary(salary0);

        var salary1 = new SalaryHeadHunter();
        salary1.setCurrency(Currency.RUR);
        salary1.setGross(true);
        salary1.setFrom(80000);
        salary1.setTo(60000); // 70 0000
        vacancies.get(1).setSalary(salary1);

        var salary2 = new SalaryHeadHunter();
        salary2.setCurrency(Currency.RUR);
        salary2.setGross(true);
        salary2.setFrom(100000);
        salary2.setTo(90000); // 95 0000
        vacancies.get(2).setSalary(salary2);
        // ===========================================


        // MORE_THAN_6 -------------------------------
        var salary3 = new SalaryHeadHunter();
        salary3.setCurrency(Currency.RUR);
        salary3.setGross(true);
        salary3.setFrom(100000);
        salary3.setTo(0); // 100 000
        vacancies.get(3).setSalary(salary3);

        var salary4 = new SalaryHeadHunter();
        salary4.setCurrency(Currency.RUR);
        salary4.setGross(true);
        salary4.setFrom(0);
        salary4.setTo(60000); // 60 000
        vacancies.get(4).setSalary(salary4);

        var salary5 = new SalaryHeadHunter();
        salary5.setCurrency(Currency.RUR);
        salary5.setGross(true);
        salary5.setFrom(90000);
        salary5.setTo(70000); // 80 0000
        vacancies.get(5).setSalary(salary5);
        // --------------------------------------------


        var listCompletableFuture = CompletableFuture.completedFuture(vacancies);
        when(headHunterRestClient.findVacanciesAsync(anyString(),anyInt(),anyInt(), anyList())).thenReturn(listCompletableFuture);
        when(exchangeCurrencyService.getUsdToRubRate()).thenReturn(80.0);

        var detailedPage0 = new VacancyDetailedPageHeadHunter();
        detailedPage0.setId(0);
        detailedPage0.setExperience(ExperienceHeadhunter.BETWEEN_1_AND_3);

        var detailedPage1 = new VacancyDetailedPageHeadHunter();
        detailedPage1.setId(1);
        detailedPage1.setExperience(ExperienceHeadhunter.BETWEEN_1_AND_3);

        var detailedPage2 = new VacancyDetailedPageHeadHunter();
        detailedPage2.setId(2);
        detailedPage2.setExperience(ExperienceHeadhunter.BETWEEN_1_AND_3);

        var detailedPage3 = new VacancyDetailedPageHeadHunter();
        detailedPage3.setId(3);
        detailedPage3.setExperience(ExperienceHeadhunter.MORE_THAN_6);

        var detailedPage4 = new VacancyDetailedPageHeadHunter();
        detailedPage4.setId(4);
        detailedPage4.setExperience(ExperienceHeadhunter.MORE_THAN_6);

        var detailedPage5 = new VacancyDetailedPageHeadHunter();
        detailedPage5.setId(5);
        detailedPage5.setExperience(ExperienceHeadhunter.MORE_THAN_6);
        var responseList = new LinkedList<VacancyDetailedPageHeadHunter>();
        responseList.add(detailedPage0);
        responseList.add(detailedPage1);
        responseList.add(detailedPage2);
        responseList.add(detailedPage3);
        responseList.add(detailedPage4);
        responseList.add(detailedPage5);

        CompletableFuture<List<VacancyDetailedPageHeadHunter>> responseListFuture = CompletableFuture.completedFuture(responseList);
        when(headHunterRestClient.getListOfDetailedVacancies(anyList())).thenReturn(responseListFuture);

        List<MarketDemand> result = headHunterService.findMarketDemandsForToday("java", City.SAMARA, "Head hunter");
        result.sort((a, b) -> Integer.compare(b.getMinYearExperience(), a.getMinYearExperience()));
        assertEquals(2, result.size());
        assertEquals(80000, result.get(0).getAverageRubGrossSalary());
        assertEquals(80000, result.get(1).getAverageRubGrossSalary());
    }

    @Test
    public void getRubledGrossAverageSalaryForVacancyTest() {
        VacancyHeadHunter vacancyItemResponse = new VacancyHeadHunter();
        SalaryHeadHunter salaryVacancyResponse = new SalaryHeadHunter();
        vacancyItemResponse.setSalary(salaryVacancyResponse);

        salaryVacancyResponse.setCurrency(Currency.RUR);
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(80000);
        salaryVacancyResponse.setGross(true);
        assertEquals(75000, headHunterService.getRubledGrossAverageSalaryForVacancy(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency(Currency.RUR);
        salaryVacancyResponse.setGross(true);
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(0);
        assertEquals(70000, headHunterService.getRubledGrossAverageSalaryForVacancy(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setFrom(82300);
        salaryVacancyResponse.setTo(83000);
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setCurrency(Currency.RUR);
        assertEquals(95000, headHunterService.getRubledGrossAverageSalaryForVacancy(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency(Currency.USD);
        salaryVacancyResponse.setGross(true);
        salaryVacancyResponse.setFrom(1100);
        salaryVacancyResponse.setTo(900);
        assertEquals(80000, headHunterService.getRubledGrossAverageSalaryForVacancy(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency(Currency.USD);
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setFrom(1100);
        salaryVacancyResponse.setTo(900);
        assertEquals(91954, headHunterService.getRubledGrossAverageSalaryForVacancy(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency(Currency.USD);
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setFrom(1000);
        salaryVacancyResponse.setTo(0);
        assertEquals(91954, headHunterService.getRubledGrossAverageSalaryForVacancy(vacancyItemResponse, 80.0));
    }

}
