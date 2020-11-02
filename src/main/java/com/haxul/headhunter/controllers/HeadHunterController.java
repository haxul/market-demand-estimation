package com.haxul.headhunter.controllers;


import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.services.HeadHunterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1/headhunter/demands")
public class HeadHunterController {

    private final HeadHunterService headHunterService;

    public HeadHunterController(HeadHunterService headHunterService) {
        this.headHunterService = headHunterService;
    }

    @GetMapping
    public List<MarketDemand> getMarketDemandsByPositionAndCity(@RequestParam String position, @RequestParam City city) throws InterruptedException, ExecutionException, TimeoutException {
        return headHunterService.findMarketDemandsForToday(position, city);
    }
}
