package com.haxul.analitics;


import com.haxul.analytics.dto.Difference;
import com.haxul.analytics.services.AnalyticService;
import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.repositories.HeadHunterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
public class AnalyticServiceTest {


    @Autowired
    private HeadHunterRepository headHunterRepository;

    @Autowired
    private AnalyticService analyticService;
    @Test
    public void testRepo() {

    }
}
