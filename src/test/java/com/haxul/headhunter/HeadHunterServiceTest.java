package com.haxul.headhunter;


import com.haxul.headhunter.models.responses.SalaryVacancyResponse;
import com.haxul.headhunter.models.responses.VacancyItemResponse;
import com.haxul.headhunter.services.HeadHunterService;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class HeadHunterServiceTest {


    @Autowired
    private HeadHunterService headHunterService;

//    @MockBean
//    private TraceRepository traceRepository;
//
//
//    @Before
//    public void executedBeforeEach() {
//        initMocks(this);
//    }


    @Test
    public void computeMarkDemandStateTest() {
        headHunterService.computeMarketDemandState("java", 78);
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
