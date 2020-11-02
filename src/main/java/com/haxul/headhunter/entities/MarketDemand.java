package com.haxul.headhunter.entities;

import com.haxul.headhunter.models.area.City;
import lombok.Data;

import java.util.Date;

@Data
public class MarketDemand {
    private String position;
    private int averageRubGrossSalary;
    private int amount;
    private Date atMoment;
    private City city;
    private int minYearExperience;
    private String source;
}
