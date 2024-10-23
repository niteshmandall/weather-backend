package com.example.weather.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId; // Change user_id to userId to follow camelCase conventions
    private String username;

    @JsonIgnore
    private String password;

    private String role;
    private boolean active;
    private String email;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private RefreshToken refreshToken;

    public Integer getUser_id() {
        return userId;
    }

    public Object getId() {
        return userId;
    }
}
