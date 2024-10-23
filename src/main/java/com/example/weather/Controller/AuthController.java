package com.example.weather.Controller;

import com.example.weather.DTO.OtpVerificationDTO;
import com.example.weather.DTO.Registration;
import com.example.weather.Entity.LoginRequest;
import com.example.weather.Entity.RefreshToken;
import com.example.weather.Entity.Users;
import com.example.weather.Models.JwtResponse;
import com.example.weather.Models.RefreshTokenRequest;
import com.example.weather.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/public")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UsernameGeneratorService usernameGeneratorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JWTService jwtService;

    @Autowired
    EmailService emailService;



    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Users user = userService.findByUsername(loginRequest.getUsername());
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Return success response, e.g., token or success message
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @PostMapping("/login2")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {

        System.out.println(loginRequest);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername());
        String jwtToken = userService.verfiy(loginRequest);
        int userId = userService.findByUsername(loginRequest.getUsername()).getUserId();

        JwtResponse jwtResponse = JwtResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken.getRefreshToken())
                .username(loginRequest.getUsername())
                .userId(userId)
                .email(userService.findByUsername(loginRequest.getUsername()).getEmail())
                .build();


        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }



    @GetMapping("/test")
    public String test() {
        System.out.println("connecton checking");
        return "public call";
    }


    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshJwtToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        System.out.println(refreshTokenRequest);

        RefreshToken newRefreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        String jwtToken = jwtService.generateToken(newRefreshToken.getUser().getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("jwtToken", jwtToken);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/registration")
    public ResponseEntity<String> register(@RequestBody Registration registrationDTO) {
        // Validate email
        if (!UserService.EmailValidator.isValidEmail(registrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email format");
        }

        // Generate OTP
        String otp = userService.generateOTP(registrationDTO.getEmail());
        System.out.println();

        // Send OTP email
        emailService.sendOtpEmail(registrationDTO.getEmail(), otp);

        return ResponseEntity.ok("OTP sent successfully");
    }


    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one digit";
        }
        if (!password.matches(".*[@#$%^&+=].*")) {
            return "Password must contain at least one special character (@#$%^&+=)";
        }
        return null; // Return null if password is valid
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationDTO otpDTO) {
        boolean isVerified = userService.verifyOtp(otpDTO.getEmail(), otpDTO.getOtp());

        String passwordValidationMessage = validatePassword(otpDTO.getPassword());
        if (passwordValidationMessage != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(passwordValidationMessage);
        }

        if (isVerified) {
            // Register the user
            userService.registerUser(otpDTO.getUsername(), otpDTO.getPassword(), otpDTO.getEmail());
            int userID = userService.findByUsername(otpDTO.getUsername()).getUserId();

            // Delete the OTP after successful verification
            userService.deleteOtp(otpDTO.getEmail());

            return ResponseEntity.ok("User registered successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtService.extractUserName(jwtToken);

        refreshTokenService.deleteByUsername(username);



        return ResponseEntity.ok("User logged out successfully");
    }


}
