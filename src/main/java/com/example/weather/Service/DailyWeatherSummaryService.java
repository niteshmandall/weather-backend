package com.example.weather.Service;


import com.example.weather.DTO.WeatherResponse;
import com.example.weather.Entity.DailyWeatherSummary;
import com.example.weather.Repository.DailyWeatherSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DailyWeatherSummaryService {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private DailyWeatherSummaryRepository dailyWeatherSummaryRepository;

    // This map stores weather data throughout the day for aggregation
    private Map<String, List<WeatherDataPoint>> cityWeatherData = new HashMap<>();

    // Collect weather data every hour
    @Scheduled(fixedRate = 3600)  // Every hour
    public void collectWeatherData() {
        List<String> cities = Arrays.asList("Delhi", "Mumbai", "Chennai","Bangalore", "Kolkata", "Hyderabad"); // Add your cities here
        for (String city : cities) {
            WeatherResponse weatherResponse = weatherService.getWeather(city);
            if (weatherResponse != null) {
                storeWeatherData(city, weatherResponse);
            }
        }
    }

    // Store hourly weather data in the map
    private void storeWeatherData(String city, WeatherResponse weatherResponse) {
        WeatherDataPoint dataPoint = new WeatherDataPoint(
                weatherResponse.getMain().getTemp() - 273.15,  // Convert Kelvin to Celsius
                weatherResponse.getMain().getTemp_max() - 273.15,
                weatherResponse.getMain().getTemp_min() - 273.15,
                weatherResponse.getWeather().get(0).getMain() // Get weather condition (e.g., "Rain", "Clear")
        );

        // Add weather data to the city list in the map
        cityWeatherData.computeIfAbsent(city, k -> new ArrayList<>()).add(dataPoint);
    }

    // At the end of the day, calculate the daily summary
    @Scheduled(cron = "0 0 0 * * *")  // At midnight every day
    public void calculateAndStoreDailySummary() {
        LocalDate today = LocalDate.now();
        for (Map.Entry<String, List<WeatherDataPoint>> entry : cityWeatherData.entrySet()) {
            String city = entry.getKey();
            List<WeatherDataPoint> weatherDataPoints = entry.getValue();

            if (!weatherDataPoints.isEmpty()) {
                double avgTemp = weatherDataPoints.stream().mapToDouble(WeatherDataPoint::getTemp).average().orElse(0.0);
                double maxTemp = weatherDataPoints.stream().mapToDouble(WeatherDataPoint::getMaxTemp).max().orElse(0.0);
                double minTemp = weatherDataPoints.stream().mapToDouble(WeatherDataPoint::getMinTemp).min().orElse(0.0);

                // Determine the dominant weather condition
                String dominantCondition = getDominantCondition(weatherDataPoints);

                // Save the daily summary to the database
                DailyWeatherSummary summary = new DailyWeatherSummary();
                summary.setCity(city);
                summary.setDate(today);
                summary.setAvgTemp(avgTemp);
                summary.setMaxTemp(maxTemp);
                summary.setMinTemp(minTemp);
                summary.setDominantCondition(dominantCondition);

                dailyWeatherSummaryRepository.save(summary);
            }
        }

        // Clear the data for the next day
        cityWeatherData.clear();
    }

    // Helper method to find the dominant weather condition
    private String getDominantCondition(List<WeatherDataPoint> weatherDataPoints) {
        Map<String, Long> conditionCount = new HashMap<>();
        for (WeatherDataPoint dataPoint : weatherDataPoints) {
            conditionCount.put(dataPoint.getCondition(), conditionCount.getOrDefault(dataPoint.getCondition(), 0L) + 1);
        }
        return Collections.max(conditionCount.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    // A simple class to store weather data points for each city
    private static class WeatherDataPoint {
        private double temp;
        private double maxTemp;
        private double minTemp;
        private String condition;

        public WeatherDataPoint(double temp, double maxTemp, double minTemp, String condition) {
            this.temp = temp;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.condition = condition;
        }

        public double getTemp() { return temp; }
        public double getMaxTemp() { return maxTemp; }
        public double getMinTemp() { return minTemp; }
        public String getCondition() { return condition; }
    }

    public List<DailyWeatherSummary> getWeatherSummariesForLast7Days(String city) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        return dailyWeatherSummaryRepository.findWeatherSummariesForLast7Days(city, startDate, endDate);
    }
}

