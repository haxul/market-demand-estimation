package com.haxul.headhunter.models.responses;

import lombok.Data;

import java.util.List;

@Data
public class VacanciesHeadHunter {
    private Integer found;
    private Integer pages;
    private Integer page;
    private List<VacancyItem> items;
}
