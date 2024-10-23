package com.example.weather.Controller;

import com.example.weather.DTO.WeatherResponse;
import com.example.weather.Entity.DailyWeatherSummary;
import com.example.weather.Service.AlertService;
import com.example.weather.Service.DailyWeatherSummaryService;
import com.example.weather.Service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private AlertService alertService;

    @Autowired
    private DailyWeatherSummaryService dailyWeatherSummaryService;

    @GetMapping("/current/{city}")
    public ResponseEntity<WeatherResponse> getCurrentWeather(@PathVariable String city) {
        System.out.println("weather check");
        WeatherResponse weather = weatherService.getWeather(city);
        if (weather != null) {
            return ResponseEntity.ok(weather);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/check-alerts")
    public ResponseEntity<String> manuallyCheckAlerts() {
        System.out.println("test");
        alertService.checkWeatherForAlerts(); // Call alert check method manually
        return ResponseEntity.ok("Weather alerts checked manually");
    }

    @GetMapping("/history/{city}")
    public List<DailyWeatherSummary> getLast7DaysWeather(@PathVariable String city) {
        return dailyWeatherSummaryService.getWeatherSummariesForLast7Days(city);
    }
}

