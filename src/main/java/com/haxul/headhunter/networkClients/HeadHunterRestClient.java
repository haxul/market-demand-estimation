package com.haxul.headhunter.networkClients;

import com.haxul.headhunter.exceptions.HeadHunterWrongResponseException;
import com.haxul.headhunter.models.responses.VacanciesResponse;
import com.haxul.headhunter.models.responses.VacancyItemResponse;
import com.haxul.headhunter.models.responses.VacancyViewPageResponse;
import com.haxul.utils.AppUtils;
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
    private final AppUtils appUtils;

    @Value("${headhunter.baseUrl}")
    @Setter
    private String baseUrl;


    public HeadHunterRestClient(RestTemplate restTemplate, AppUtils appUtils) {
        this.appUtils = appUtils;
        this.restTemplate = restTemplate;
    }


    public CompletableFuture<List<VacancyItemResponse>> findVacanciesAsync(String position, int areaId, int page, List<VacancyItemResponse> vacancies) {
        return CompletableFuture.supplyAsync(() -> findVacancies(position, areaId, page, vacancies));
    }

    public List<VacancyItemResponse> findVacancies(String position, int areaId, int page, List<VacancyItemResponse> vacancies) {
        try {
            final String url = baseUrl + "vacancies?text=\"" + position + "\"&area=" + areaId + "&page=" + page + "&only_with_salary=true";
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

    public CompletableFuture<VacancyViewPageResponse> getDetailedVacancyDataById(int id) {
        final String url = baseUrl + "vacancies/" + id;
        return CompletableFuture
                .supplyAsync(() -> restTemplate.getForObject(url, VacancyViewPageResponse.class))
                .exceptionally(appUtils::handleError);
    }
}
