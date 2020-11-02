package com.haxul.headhunter.models.hhApiResponses;

import lombok.Data;

import java.util.List;

@Data
public class VacanciesHeadHunter {
    private Integer found;
    private Integer pages;
    private Integer page;
    private List<VacancyHeadHunter> items;
}
