package com.example.weather.Repository;


import com.example.weather.Entity.DailyWeatherSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyWeatherSummaryRepository extends JpaRepository<DailyWeatherSummary, Long> {

    DailyWeatherSummary findByCityAndDate(String city, LocalDate date);

    // Find weather summaries for a specific city and within the last 7 days
    @Query("SELECT d FROM DailyWeatherSummary d WHERE d.city = :city AND d.date BETWEEN :startDate AND :endDate")
    List<DailyWeatherSummary> findWeatherSummariesForLast7Days(String city, LocalDate startDate, LocalDate endDate);
}

