package com.haxul.headhunter.networkClients;

import com.haxul.headhunter.exceptions.HeadHunterWrongResponseException;
import com.haxul.headhunter.models.responses.VacanciesResponse;
import com.haxul.headhunter.models.responses.VacancyItemResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class HeadHunterRestClient {

    private final RestTemplate restTemplate;

    @Value("${headhunter.baseUrl}")
    @Setter
    private String baseUrl;


    public HeadHunterRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public CompletableFuture<List<VacancyItemResponse>> findVacanciesAsync(String position, int areaId, int page, List<VacancyItemResponse> vacancies) {
        return CompletableFuture.supplyAsync(() -> findVacancies(position, areaId, page, vacancies));
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
