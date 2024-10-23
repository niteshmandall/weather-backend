package com.example.weather.Controller;

import com.example.weather.Entity.Users;
import com.example.weather.Models.AlertConfig;
import com.example.weather.Repository.AlertConfigRepository;
import com.example.weather.Repository.UserRepo;
import com.example.weather.Service.AlertService;
import com.example.weather.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
public class AlertConfigController {

    @Autowired
    private AlertConfigRepository alertConfigRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private AlertService alertService;

    @Autowired
    JWTService jwtService;

    @PostMapping
    public ResponseEntity<String> setAlert(@RequestBody AlertConfig alertConfig,  @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtService.extractUserName(jwtToken);
        Users currUser = userRepository.findByUsername(username);
        int userId = currUser.getUserId();
        System.out.println("ALert config: " + alertConfig);

        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()) {
            try {
                alertConfig.setUser(user.get());
                alertConfigRepository.save(alertConfig);
                return ResponseEntity.status(HttpStatus.CREATED).body("Alert configuration saved successfully");

            } catch (DataIntegrityViolationException e) {
                // Handle duplicate entry exception (constraint violation)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Alert configuration already exists");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping("/user")
    public ResponseEntity<List<AlertConfig>> getUserAlerts(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtService.extractUserName(jwtToken);
        Users currUser = userRepository.findByUsername(username);
        int userId = currUser.getUserId();


        List<AlertConfig> alerts = alertConfigRepository.findAll()
                .stream()
                .filter(alert -> alert.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAlert(
            @RequestParam Long userId,
            @RequestParam String city,
            @RequestParam double thresholdTemp) {
        String result = alertService.deleteAlertConfig(userId, city, thresholdTemp);
        return ResponseEntity.ok(result);
    }
}
