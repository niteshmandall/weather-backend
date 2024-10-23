package com.example.weather.Models;


import com.example.weather.Entity.Users;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AlertConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private double thresholdTemp;

    @ManyToOne
    private Users user;

    private int thresholdExceedCount = 0;

}