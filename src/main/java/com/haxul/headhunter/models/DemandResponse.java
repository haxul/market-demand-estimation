package com.haxul.headhunter.models;

import com.haxul.headhunter.entities.MarketDemand;
import lombok.Data;

import java.util.List;

@Data
public class DemandResponse {
    private List<MarketDemand> items;
    private int foundVacancies;
}
