package com.example.weather.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UsernameGeneratorService {

    @Autowired
    private UserService userService;  // Assuming you have a UserService that interacts with the database

    private final Random random = new Random();

    public String generateUniqueUsername(String email) {
        // Step 1: Extract base username from the email (everything before the '@')
        String baseUsername = email.split("@")[0];

        // Step 2: Initialize the generated username with the base username
        String generatedUsername = baseUsername;

        // Step 3: Check if the username already exists in the database
        while (userService.usernameExists(generatedUsername)) {
            // If the username exists, append random digits to the base username
            generatedUsername = baseUsername + random.nextInt(10000); // Appending a random 4-digit number
        }

        // Step 4: Return the unique username
        return generatedUsername;
    }
}

