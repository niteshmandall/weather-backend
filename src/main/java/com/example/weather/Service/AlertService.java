package com.example.weather.Service;

import com.example.weather.DTO.WeatherResponse;
import com.example.weather.Entity.Users;
import com.example.weather.Models.AlertConfig;
import com.example.weather.Repository.AlertConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AlertService {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private AlertConfigRepository alertConfigRepository;

    @Autowired
    private EmailService emailService;


    @Scheduled(fixedRate = 30000)  // Every 5 minutes (300,000 ms)
    public void checkWeatherForAlerts() {
        List<AlertConfig> allAlerts = alertConfigRepository.findAll(); // Get all alert configurations

        for (AlertConfig alert : allAlerts) {
            // Fetch weather data for the city
            WeatherResponse weatherData = weatherService.getWeather(alert.getCity());

            if (weatherData != null) {
                // Convert temperature from Kelvin to Celsius
                double currentTemp = weatherData.getMain().getTemp() - 273.15;

                // Check if the current temperature exceeds the user's threshold
                if (currentTemp > alert.getThresholdTemp()) {
                    // Increment the threshold exceed count
                    alert.setThresholdExceedCount(alert.getThresholdExceedCount() + 1);

                    // Only send the alert if the threshold has been exceeded more than twice
                    if (alert.getThresholdExceedCount() > 2) {
                        System.out.println("Threshold exceeded more than twice. Sending alert...");
                        sendAlert(alert.getUser(), alert.getCity(), currentTemp, alert.getThresholdTemp());

                        // Reset the counter after sending the alert
                        alert.setThresholdExceedCount(0);
                    }

                    // Save the updated alert configuration back to the repository
                    alertConfigRepository.save(alert);
                }
            }
        }
    }

    // This method sends an alert to the user
    private void sendAlert(Users user, String city, double currentTemp, double thresholdTemp) {
        // Construct the email message
        String subject = "Weather Alert: Temperature Threshold Exceeded!";
        String message = String.format(
                "Hello %s,\n\nThe current temperature in %s is %.2f°C, which exceeds your threshold of %.2f°C.\n\nStay safe!",
                user.getUsername(), city, currentTemp, thresholdTemp
        );

        // Send the email using EmailService
        emailService.sendEmail(user.getEmail(), subject, message);

        // Optionally, you can also log the alert or send notifications through other means (SMS, Push notifications, etc.)
        System.out.println("Alert sent to " + user.getEmail() + " for city: " + city + " (Current Temp: " + currentTemp + "°C, Threshold: " + thresholdTemp + "°C)");
    }


    // This method deletes an alert configuration for a specific user
    public String deleteAlertConfig(Long userId, String city, double thresholdTemp) {
        // Find the alert configuration for the user, city, and threshold temperature
        AlertConfig alertConfig = alertConfigRepository.findByUserIdAndCityAndThresholdTemp(userId, city, thresholdTemp);

        if (alertConfig != null) {
            // Delete the alert configuration from the repository
            alertConfigRepository.delete(alertConfig);
            return "Alert configuration deleted successfully!";
        } else {
            return "Alert configuration not found!";
        }
    }
}
