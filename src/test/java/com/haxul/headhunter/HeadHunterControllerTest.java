package com.haxul.headhunter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.haxul.headhunter.controllers.HeadHunterController;
import com.haxul.headhunter.services.HeadHunterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(HeadHunterController.class)
public class HeadHunterControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HeadHunterService headHunterService;


    @Test
    public void getMarketDemandsByPositionAndCityTest() {

    }


}
