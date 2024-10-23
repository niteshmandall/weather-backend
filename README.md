
# Weather Monitoring System Backend

This is the backend for the Weather Monitoring System, built using **Spring Boot** and **MySQL**, which fetches weather data from the **OpenWeatherMap API**. It provides user authentication, alert management, and weather summary features.

## Features

- **JWT Authentication** for secure API access.
- **CORS Configuration** for frontend communication.
- **Alert System** for weather notifications.
- **Weather Data Fetching** from OpenWeatherMap API.
- **Daily Weather Summary** for tracking historical data.
- **User Registration and Login** with OTP verification.
  
## Technology Stack

- **Java 17**
- **Spring Boot**
- **Spring Security** for JWT-based authentication.
- **MySQL** as the database.
- **Hibernate** for ORM.
- **OpenWeatherMap API** for fetching weather data.

## Configuration

### Application Properties

Ensure the following values are set in `application.properties`:

\`\`\`properties
# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/weather
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update

# OpenWeatherMap API
openweather.api.key=YOUR_API_KEY_HERE
openweather.api.url=http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s

# Email Service (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
\`\`\`

### CORS Configuration

The backend is configured to accept requests from the frontend hosted at `http://localhost:3000` and other specified origins.

You can update the allowed origins in `SecurityConfig.java`:

\`\`\`java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://your-frontend-domain.com"));
\`\`\`

### JWT Configuration

The application uses **JWT tokens** for authentication. In requests that need user validation, a valid JWT token must be included in the headers:

\`\`\`
Authorization: Bearer <your_token_here>
\`\`\`

### Running the Application

1. **Clone the repository:**

    \`\`\`bash
    git clone https://github.com/your-repo/weather-monitor-backend.git
    cd weather-monitor-backend
    \`\`\`

2. **Configure Database:**
   Ensure MySQL is running locally, and the database schema is created as per the `application.properties`.

3. **Run the application:**

    \`\`\`bash
    ./mvnw spring-boot:run
    \`\`\`

4. **Access API:**
   The API will be available at: `http://localhost:8080`.

## API Endpoints

### Public Endpoints

- **POST /api/public/login**  
  Login with username and password.

- **POST /api/public/registration**  
  Register a new user.

- **POST /api/public/verify-otp**  
  Verify OTP and complete registration.

- **POST /api/public/refresh**  
  Refresh JWT token.

### Authenticated Endpoints (JWT Required)

- **GET /api/weather/current/{city}**  
  Get current weather for a specific city.

- **GET /api/weather/history/{city}**  
  Get weather summaries for the last 7 days for a specific city.

- **POST /api/alerts**  
  Set an alert for specific weather conditions.

- **GET /api/alerts/user**  
  Get user-specific alerts.

- **DELETE /api/alerts/delete**  
  Delete a specific weather alert.

## JWT Role-Based Authorization

- **USER**: Can access `/api/test` and perform basic weather checks.
- **ADMIN**: Can access `/api/admin/**` endpoints.

## Authentication Flow

1. User registers using the `registration` endpoint.
2. An OTP is sent to the registered email.
3. Upon OTP verification, the user can log in and will receive a JWT token.
4. Use the JWT token for all secured API requests.

---

### Contributing

Feel free to open issues or submit PRs if you want to contribute to this project.

---

### License

This project is licensed under the MIT License.
