package com.haxul.headhunter.networkClients;

import com.haxul.headhunter.exceptions.HeadHunterWrongResponseException;
import com.haxul.headhunter.models.responses.VacanciesHeadHunter;
import com.haxul.headhunter.models.responses.VacancyItem;
import com.haxul.headhunter.models.responses.VacancyViewPageHeadHunter;
import com.haxul.utils.AppUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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


    public CompletableFuture<List<VacancyItem>> findVacanciesAsync(String position, int areaId, int page, List<VacancyItem> vacancies) {
        return CompletableFuture.supplyAsync(() -> findVacancies(position, areaId, page, vacancies));
    }

    public List<VacancyItem> findVacancies(String position, int areaId, int page, List<VacancyItem> vacancies) {
        try {
            final String url = baseUrl + "vacancies?text=\"" + position + "\"&area=" + areaId + "&page=" + page + "&only_with_salary=true";
            VacanciesHeadHunter response = restTemplate.getForObject(url, VacanciesHeadHunter.class);
            if (response == null) throw new HeadHunterWrongResponseException("vacanciesResponse is null");
            vacancies.addAll(response.getItems());
            if (response.getPages() == page + 1) return vacancies;
            return findVacancies(position, areaId, ++page, vacancies);
        } catch (Exception e) {
            log.error("HeadHunterService error: " + e);
            throw new HeadHunterWrongResponseException(e);
        }
    }

    private CompletableFuture<VacancyViewPageHeadHunter> getDetailedVacancyDataById(int id) {
        final String url = baseUrl + "vacancies/" + id;
        return CompletableFuture
                .supplyAsync(() -> restTemplate.getForObject(url, VacancyViewPageHeadHunter.class))
                .exceptionally(appUtils::handleError);
    }


    public CompletableFuture<List<VacancyViewPageHeadHunter>> getListOfDetailedVacancies(List<VacancyItem> vacanciesWithoutExperience) {
        List<CompletableFuture<VacancyViewPageHeadHunter>> detailedVacancies = vacanciesWithoutExperience
                .stream()
                .map(vacancyWithoutExperience -> getDetailedVacancyDataById(vacancyWithoutExperience.getId()))
                .collect(Collectors.toList());

        var allDetailedVacanciesAreDownloadedFuture = CompletableFuture.allOf(detailedVacancies.toArray(new CompletableFuture[detailedVacancies.size()]));

        return allDetailedVacanciesAreDownloadedFuture.thenApply(future -> detailedVacancies
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }
}
