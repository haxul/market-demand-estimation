package com.haxul.headhunter;


import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.models.currency.Currency;
import com.haxul.headhunter.models.responses.SalaryHeadHunter;
import com.haxul.headhunter.models.responses.VacancyItem;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class HeadHunterServiceTest {

    @Value("${headhunter.baseUrl}")
    private String baseUrl;

    @Autowired
    private HeadHunterService headHunterService;
//
//    @MockBean
//    private HeadHunterRestClient headHunterRestClient;
//
//    @MockBean
//    private ExchangeCurrencyService exchangeCurrencyService;

    @BeforeEach
    public void executedBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    public void computeMarketDemandStateNetworkTest() throws InterruptedException, ExecutionException, TimeoutException {
        var restClient = new HeadHunterRestClient(new RestTemplate(), new AppUtils());
        restClient.setBaseUrl(baseUrl);
        List<VacancyItem> list = restClient.findVacanciesAsync("java", City.SAMARA.getId(), 0, new LinkedList<>()).get(5, TimeUnit.SECONDS);
        assertNotEquals(0, list.size());
    }

    // TODO fix test
    @Test
    public void computeMarkDemandStateTest() throws InterruptedException, ExecutionException, TimeoutException {
        VacancyItem vacancyItemResponse = new VacancyItem();
        SalaryHeadHunter salaryVacancyResponse = new SalaryHeadHunter();
        vacancyItemResponse.setSalary(salaryVacancyResponse);
        salaryVacancyResponse.setCurrency(Currency.RUR);
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(80000);
        salaryVacancyResponse.setGross(true);

        List<VacancyItem> list = new LinkedList<>();
        list.add(vacancyItemResponse);
        var future = CompletableFuture.completedFuture(list);
//        when(headHunterRestClient.findVacanciesAsync(anyString(), anyInt(), anyInt(), anyList())).thenReturn(future);
//        when(exchangeCurrencyService.getUsdToRubRate()).thenReturn(80.0);
        headHunterService.computeMarketDemandState("java", City.SAMARA);
    }

    @Test
    public void getRubleAverageSalaryTest() {
        VacancyItem vacancyItemResponse = new VacancyItem();
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
