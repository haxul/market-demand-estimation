package com.haxul.headhunter.models.responses;

import lombok.Data;

import java.util.List;

@Data
public class VacanciesResponse {
    private Integer found;
    private Integer pages;
    private Integer page;
    private List<VacancyItemResponse> items;
}
