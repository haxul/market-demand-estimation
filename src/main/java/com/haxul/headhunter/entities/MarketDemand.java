package com.haxul.headhunter.entities;

import com.haxul.headhunter.models.area.City;
import lombok.Data;

import java.util.Date;

/**
 * TODO market demand should contain experience
 */
@Data
public class MarketDemand {
    private String position;
    private int averageGrossSalary;
    private int amount;
    private Date atMoment;
    private City city;
}
