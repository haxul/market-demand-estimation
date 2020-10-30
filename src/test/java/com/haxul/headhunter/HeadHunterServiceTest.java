package com.haxul.headhunter;


import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.models.responses.SalaryVacancyResponse;
import com.haxul.headhunter.models.responses.VacancyItemResponse;
import com.haxul.headhunter.networkClients.HeadHunterRestClient;
import com.haxul.headhunter.services.HeadHunterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class HeadHunterServiceTest {


    @Autowired
    private HeadHunterService headHunterService;

    @MockBean
    private HeadHunterRestClient headHunterRestClient;

    @BeforeEach
    public void executedBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void computeMarkDemandStateTest() {
        VacancyItemResponse vacancyItemResponse = new VacancyItemResponse();
        SalaryVacancyResponse salaryVacancyResponse = new SalaryVacancyResponse();
        vacancyItemResponse.setSalary(salaryVacancyResponse);
        salaryVacancyResponse.setCurrency("RUR");
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(80000);
        salaryVacancyResponse.setGross(true);

        List<VacancyItemResponse> list = new LinkedList<>();
        list.add(vacancyItemResponse);
        when(headHunterRestClient.findVacancies(anyString(), anyInt(), anyInt() ,anyList())).thenReturn(list);
        MarketDemand marketDemand = headHunterService.computeMarketDemandState("java", City.SAMARA);
        assertNotNull(marketDemand);
        assertEquals(75000, marketDemand.getAverageGrossSalary());
    }

    @Test
    public void getRubleAverageSalaryTest() {
        VacancyItemResponse vacancyItemResponse = new VacancyItemResponse();
        SalaryVacancyResponse salaryVacancyResponse = new SalaryVacancyResponse();
        vacancyItemResponse.setSalary(salaryVacancyResponse);

        salaryVacancyResponse.setCurrency("RUR");
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(80000);
        salaryVacancyResponse.setGross(true);
        assertEquals(75000, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency("RUR");
        salaryVacancyResponse.setGross(true);
        salaryVacancyResponse.setFrom(70000);
        salaryVacancyResponse.setTo(0);

        assertEquals(70000, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setFrom(82300);
        salaryVacancyResponse.setTo(83000);
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setCurrency("RUR");
        assertEquals(95000, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency("USD");
        salaryVacancyResponse.setGross(true);
        salaryVacancyResponse.setFrom(1100);
        salaryVacancyResponse.setTo(900);

        assertEquals(80000, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency("USD");
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setFrom(1100);
        salaryVacancyResponse.setTo(900);

        assertEquals(91954, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));

        salaryVacancyResponse.setCurrency("USD");
        salaryVacancyResponse.setGross(false);
        salaryVacancyResponse.setFrom(1000);
        salaryVacancyResponse.setTo(0);

        assertEquals(91954, headHunterService.getRubleAverageSalary(vacancyItemResponse, 80.0));
    }

}
