package com.example.weather.DTO;


import lombok.Data;

@Data
public class OtpVerificationDTO {
    private String email;
    private String otp;
    private String username;
    private String password;
}


