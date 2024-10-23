package com.example.weather.Models;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JwtResponse {

    private String jwtToken;

    private String username;

    private String refreshToken;

    private int userId;

    private String email;
}
