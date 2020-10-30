package com.haxul.headhunter;


import com.haxul.exchangeCurrency.services.ExchangeCurrencyService;
import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.models.currency.Currency;
import com.haxul.headhunter.models.responses.SalaryVacancyResponse;
import com.haxul.headhunter.models.responses.VacancyItemResponse;
import com.haxul.headhunter.networkClients.HeadHunterRestClient;
import com.haxul.headhunter.services.HeadHunterService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    public void computeMarketDemandStateNetworkTest() throws InterruptedException, ExecutionException, TimeoutException {
        var restClient = new HeadHunterRestClient(new RestTemplate());
        restClient.setBaseUrl(baseUrl);
        List<VacancyItemResponse> list = restClient.findVacanciesAsync("java", City.SAMARA.getId(), 0, new LinkedList<>()).get(5, TimeUnit.SECONDS);
        assertNotEquals(0, list.size());
    }

    @Test
    public void computeMarkDemandStateTest() throws InterruptedException, ExecutionException, TimeoutException {
        VacancyItemResponse vacancyItemResponse = new VacancyItemResponse();
        SalaryVacancyResponse salaryVacancyResponse = new SalaryVacancyResponse();
        vacancyItemResponse.setSalary(salaryVacancyResponse);
        salaryVacancyResponse.setCurrency(Currency.RUR);
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(80000);
        salaryVacancyResponse.setGross(true);

        List<VacancyItemResponse> list = new LinkedList<>();
        list.add(vacancyItemResponse);
        var future = CompletableFuture.completedFuture(list);
        when(headHunterRestClient.findVacanciesAsync(anyString(), anyInt(), anyInt() ,anyList())).thenReturn(future);
        when(exchangeCurrencyService.getUsdToRubRate()).thenReturn(80.0);
        MarketDemand marketDemand = headHunterService.computeMarketDemandState("java", City.SAMARA);
        assertNotNull(marketDemand);
        assertEquals(75000, marketDemand.getAverageGrossSalary());
    }

    @Test
    public void getRubleAverageSalaryTest() {
        VacancyItemResponse vacancyItemResponse = new VacancyItemResponse();
        SalaryVacancyResponse salaryVacancyResponse = new SalaryVacancyResponse();
        vacancyItemResponse.setSalary(salaryVacancyResponse);

        salaryVacancyResponse.setCurrency(Currency.RUR);
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(80000);
        salaryVacancyResponse.setGross(true);
        assertEquals(75000, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency(Currency.RUR);
        salaryVacancyResponse.setGross(true);
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(0);
        assertEquals(70000, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setFrom(82300);
        salaryVacancyResponse.setTo(83000);
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setCurrency(Currency.RUR);
        assertEquals(95000, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency(Currency.USD);
        salaryVacancyResponse.setGross(true);
        salaryVacancyResponse.setFrom(1100);
        salaryVacancyResponse.setTo(900);
        assertEquals(80000, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency(Currency.USD);
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setFrom(1100);
        salaryVacancyResponse.setTo(900);
        assertEquals(91954, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency(Currency.USD);
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setFrom(1000);
        salaryVacancyResponse.setTo(0);
        assertEquals(91954, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));
    }

}
