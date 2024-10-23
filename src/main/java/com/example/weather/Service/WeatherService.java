package com.example.weather.Service;

import com.example.weather.DTO.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${openweather.api.key}")
    private String apiKey;


    private final String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";

    public WeatherResponse getWeather(String city) {
        String url = String.format(weatherUrl, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(url, WeatherResponse.class);
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Failed to fetch weather data for city: " + city);
            return null;
        }
    }
}

