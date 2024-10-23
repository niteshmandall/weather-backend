package com.example.weather.Repository;

import com.example.weather.Models.AlertConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertConfigRepository extends JpaRepository<AlertConfig, Long> {
    List<AlertConfig> findByCity(String city);

    AlertConfig findByUserIdAndCityAndThresholdTemp(Long userId, String city, double thresholdTemp);
}
