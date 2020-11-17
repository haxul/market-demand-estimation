package com.haxul.analytics.services;


import com.haxul.analytics.DateType;
import com.haxul.analytics.dto.Difference;
import com.haxul.analytics.exceptions.UnSupportedDateTypeException;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.repositories.HeadHunterRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticService {

    private final HeadHunterRepository headHunterRepository;
//TODO create analytics
    @SneakyThrows
    public Difference getDifference(DateType dateType, int previousDateItems, City city, String position) {
        switch (dateType) {
            case DAY: return null;
            case MONTH: return null;
            default: throw new UnSupportedDateTypeException();
        }
    }
}
