package com.haxul.headhunter.repositories;

import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.area.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HeadHunterRepository extends JpaRepository<MarketDemand, Long> {

    List<MarketDemand> findByPositionAndCityAndAtMoment(String position, City city, Date date);

    @Query(value = "SELECT * FROM market_demands WHERE position= ?1" +
            " AND city= ?2" +
            " AND at_moment > clock_timestamp() - (interval '1' day) * ?3", nativeQuery = true)
    List<MarketDemand> findAllAfterDays(String position, String city, int days);
}
