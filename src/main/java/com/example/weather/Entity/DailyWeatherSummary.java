package com.example.weather.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "daily_weather_summary")
public class DailyWeatherSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    private LocalDate date;

    private double avgTemp;

    private double maxTemp;

    private double minTemp;

    private String dominantCondition;  // E.g., "Clear", "Rain", "Clouds", etc.

    // Add any additional fields you might need
}

