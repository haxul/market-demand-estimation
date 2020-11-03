package com.haxul.headhunter.repositories;

import com.haxul.headhunter.entities.MarketDemand;
import com.haxul.headhunter.models.area.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HeadHunterRepository extends JpaRepository<MarketDemand, Long> {

    List<MarketDemand> findByPositionAndCityAndAtMoment(String position, City city, Date date);
}
