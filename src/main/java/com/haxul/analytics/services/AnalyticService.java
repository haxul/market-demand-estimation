package com.haxul.analytics.services;


import com.haxul.analytics.DateType;
import com.haxul.analytics.Direction;
import com.haxul.analytics.dto.Difference;
import com.haxul.analytics.exceptions.UnSupportedDateTypeException;
import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.area.City;
import com.haxul.headhunter.repositories.HeadHunterRepository;
import com.haxul.headhunter.services.HeadHunterService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticService {

    private final HeadHunterRepository headHunterRepository;
    private final HeadHunterService headHunterService;

    // TODO write tests
    @SneakyThrows
    public Difference getDifference(DateType dateType, int amount, City city, String position) {
        switch (dateType) {
            case DAY: return computeDifference(amount, city, position);
            case MONTH: return computeDifference(computeDays(amount), city, position);
            default: throw new UnSupportedDateTypeException();
        }
    }

    private int computeDays(int monthsCount) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -monthsCount);
        Date prevDate = cal.getTime();
        return (int) Duration.between(prevDate.toInstant(), new Date().toInstant()).toDays();
    }

    @SneakyThrows
    private Difference computeDifference(int days, City city, String position) {
        List<MarketDemand> demands = headHunterRepository.findAllAfterDays(position, city.toString(), days);
        if (demands.isEmpty()) return new Difference(0, Direction.NO_CHANGE, days);

        List<MarketDemand> demandsNotToday = new LinkedList<>();
        List<MarketDemand> demandsToday = new LinkedList<>();

        for (var demand : demands) {
            if (isSameDay(demand.getAtMoment(), new Date())) demandsToday.add(demand);
            else demandsNotToday.add(demand);
        }

        if (demandsToday.isEmpty()) {
            demandsToday = headHunterService.findMarketDemandsForToday(position, city, "HeadHunter");
        }

        int todayAverage = demandsToday.stream().mapToInt(MarketDemand::getAverageRubGrossSalary).sum() / demandsToday.size();
        int notTodayAverage = demandsNotToday.stream().mapToInt(MarketDemand::getAverageRubGrossSalary).sum() / demandsNotToday.size();

        int differencePercentage = (notTodayAverage * 100) / todayAverage;

        Direction direction = differencePercentage > 0 ? Direction.UP
                : differencePercentage < 0 ? Direction.DOWN
                : Direction.NO_CHANGE;


        return new Difference(differencePercentage, direction, days);
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }
}
