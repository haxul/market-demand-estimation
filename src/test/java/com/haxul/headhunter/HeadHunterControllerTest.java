package com.haxul.headhunter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.haxul.headhunter.controllers.HeadHunterController;
import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.DemandResponse;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.services.HeadHunterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(HeadHunterController.class)
public class HeadHunterControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HeadHunterService headHunterService;


    private final List<MarketDemand> list = new LinkedList<>();

    @BeforeEach
    public void invoke() {
        var marketDemand0 = new MarketDemand();
        marketDemand0.setSource("head hunter");
        marketDemand0.setCity(City.SAMARA);
        marketDemand0.setAmount(10);
        marketDemand0.setAtMoment(new Date());
        marketDemand0.setPosition("java");
        marketDemand0.setAverageRubGrossSalary(20000);
        marketDemand0.setMinYearExperience(0);


        var marketDemand1 = new MarketDemand();
        marketDemand1.setSource("head hunter");
        marketDemand1.setCity(City.SAMARA);
        marketDemand1.setAmount(20);
        marketDemand1.setAtMoment(new Date());
        marketDemand1.setPosition("java");
        marketDemand1.setAverageRubGrossSalary(80000);
        marketDemand1.setMinYearExperience(3);


        var marketDemand2 = new MarketDemand();
        marketDemand2.setSource("head hunter");
        marketDemand2.setCity(City.SAMARA);
        marketDemand2.setAmount(8);
        marketDemand2.setAtMoment(new Date());
        marketDemand2.setPosition("java");
        marketDemand2.setAverageRubGrossSalary(40000);
        marketDemand2.setMinYearExperience(1);

        list.add(marketDemand0);
        list.add(marketDemand1);
        list.add(marketDemand2);
    }

    @Test
    public void getMarketDemandsByPositionAndCityTest() throws Exception {

        when(headHunterService.findMarketDemandsForToday(anyString(), any(), anyString())).thenReturn(list);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/headhunter/demands?position=java&city=samara")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        var mvcResult = mockMvc.perform(requestBuilder).andReturn();
        var response = mvcResult.getResponse();
        assertEquals(200, response.getStatus());
        String body = response.getContentAsString();
        DemandResponse result = objectMapper.readValue(body, DemandResponse.class);
        assertNotNull(result);
        assertEquals(38, result.getFoundVacancies());
    }
}
