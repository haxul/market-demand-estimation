package com.haxul.headhunter.networkClients;

import com.haxul.headhunter.exceptions.HeadHunterWrongResponseException;
import com.haxul.headhunter.models.responses.VacanciesResponse;
import com.haxul.headhunter.models.responses.VacancyItemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class HeadHunterRestClient {

    private final RestTemplate restTemplate;

    @Value("${headhunter.baseUrl}")
    private String baseUrl;


    public HeadHunterRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<VacancyItemResponse> findVacancies(String position, int areaId, int page, List<VacancyItemResponse> vacancies) {
        try {
            final String url = baseUrl + "vacancies?text=\"" + position + "\"&area=" + areaId + "&page=" + page;
            VacanciesResponse response = restTemplate.getForObject(url, VacanciesResponse.class);
            if (response == null) throw new HeadHunterWrongResponseException("vacanciesResponse is null");
            vacancies.addAll(response.getItems());
            if (response.getPages() == page + 1) return vacancies;
            return findVacancies(position, areaId, ++page, vacancies);
        } catch (Exception e) {
            log.error("HeadHunterService error: " + e);
            throw new HeadHunterWrongResponseException(e);
        }
    }
}
