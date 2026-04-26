# Product Recommendation Service

The **Product Recommendation Service** is a Spring Boot application designed to provide personalized product recommendations for users based on their profiles, purchase history, and preferences. It integrates with external services (User Profile and Product Catalog) and allows users to submit feedback on the recommendations they receive.

## Features

- **Personalized Recommendations**: Uses multiple strategies to determine the best product categories for a user.
- **Asynchronous Processing**: Fetches product data from external catalogs in parallel to optimize response times.
- **Feedback Loop**: Collects user feedback (likes/dislikes) on recommendations and stores them for future improvements.
- **Resilience & High Concurrency**: Implements Circuit Breakers, Retries, and Bulkheads using **Resilience4j**, and leverages **Java 21 Virtual Threads** for high-traffic scenarios.
- **Smart Caching**: Tailored caching strategies using **Caffeine** to balance data freshness and performance.
- **API Documentation**: Interactive API documentation provided by **Swagger/OpenAPI**.
- **Database Migrations**: Managed by **Flyway**.
- **Containerized Environment**: Easy local setup using **Docker Compose**.

---

## Architecture & How It Works

### 1. Recommendation Flow
When a request is made to `/api/recommendations/{userId}`:
1.  **Fetch User Profile**: The service calls the external **User Profile Service** to get the user's data (preferences, purchase history). This call is protected by a **Bulkhead** and utilizes a long-lived cache.
2.  **Determine Categories**: It applies a list of prioritized categories based on user preferences and behavior.
3.  **Fetch Products**: For each identified category, it fetches products from the **Product Catalog Service** asynchronously using `CompletableFuture`. These calls are protected by a **Circuit Breaker** and use a short-lived cache to ensure pricing accuracy.
4.  **Map & Respond**: The results are filtered by price and availability, aggregated, and returned as a list of recommended products.

### 2. Feedback Mechanism
Users can submit feedback via `/api/recommendations/feedback`. This feedback (product ID, user ID, like/dislike type, and optional comments) is stored in a PostgreSQL database with automatic JPA auditing.

### 3. Resilience and Performance
-   **Retries**: Configured for the User Profile service to handle transient network issues.
-   **Circuit Breaker**: Configured for the Product Catalog service to prevent cascading failures when the catalog is down.
-   **Bulkhead**: Isolates the User Profile service to prevent it from saturating system resources during slow responses.
-   **Virtual Threads**: Enabled to handle 1000+ concurrent requests efficiently by minimizing thread blocking overhead.
-   **Differential Caching**:
    -   **User Profile**: 24-hour TTL (Low volatility).
    -   **Product Catalog**: 2-minute TTL (High volatility due to real-time pricing and inventory).
-   **Timeouts**: Specialized timeouts for each service (2.5s for User Profile, 1s for Product Catalog).

---

## Tech Stack

-   **Java 21**
-   **Spring Boot 3.4.3**
-   **Spring Data JPA**
-   **PostgreSQL** (Database)
-   **Flyway** (Migrations)
-   **Resilience4j** (Resilience)
-   **WireMock** (Mocking external services)
-   **Lombok** (Boilerplate reduction)
-   **SpringDoc OpenAPI** (Documentation)

---

## Getting Started

### Prerequisites
-   Java 21
-   Docker and Docker Compose

### Running Locally with Docker
The easiest way to run the infrastructure (Database, WireMock, Prometheus, Grafana, SonarQube) is via Docker Compose:

```bash
cd local
docker-compose up -d
```

-   **PostgreSQL**: Port 5432
-   **WireMock**: Port 8081 (Mocks the external services)
-   **Prometheus**: Port 9090
-   **Grafana**: Port 3000
-   **SonarQube**: Port 9000

### Running the Application
You can run the application using Gradle:

```bash
./gradlew bootRun
```
The service will be available at `http://localhost:8080`.

### API Documentation
Once the app is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

---

## API Endpoints

### Recommendations
-   **GET** `/api/recommendations/{userId}`
    -   Fetches a list of recommended products for the given user.

### Feedback
-   **POST** `/api/recommendations/feedback`
    -   Submits feedback for a recommendation.
    -   **Body**:
        ```json
        {
          "userId": "123",
          "productId": "456",
          "feedback": "LIKE",
          "comment": "Great suggestion!"
        }
        ```

---

## How to Test

### Automated Tests
The project includes unit and integration tests using JUnit 5 and WireMock for mocking external API dependencies.

To run all tests:
```bash
./gradlew test
```

### Manual Testing with WireMock
The `local/wiremock/mappings` directory contains predefined responses for external services. You can modify these to test different scenarios (success, not found, error).

---

## Monitoring & Quality Assurance

### Prometheus & Metrics
The application exposes metrics in Prometheus format at the following endpoint:
- **Metrics Endpoint**: `http://localhost:8080/actuator/prometheus`

You can also access the Prometheus UI to query metrics directly:
- **Prometheus UI**: `http://localhost:9090`

### Grafana Dashboards
Grafana is pre-configured with a datasource and a dashboard for monitoring the Spring Boot application.
- **URL**: `http://localhost:3000`
- **Credentials**: `admin` / `admin123`
- **Metrics**: Navigate to "Metrics" -> See real-time application metrics.

### SonarQube Code Quality
SonarQube is used to perform static code analysis and ensure code quality. The project currently maintains **98% code coverage**.

- **URL**: `http://localhost:9000`
- **Credentials**: `admin` / `admin` (You may be prompted to change the password on the first login).
- **Authentication**: When running via CLI, use `-Dsonar.login=admin -Dsonar.password=admin` or generate a token in the SonarQube UI.

To run a Sonar scan with coverage report, ensure the infrastructure is running and execute:
```bash
./gradlew clean jacocoTestReport sonar -Dsonar.login=admin -Dsonar.password=admin123
```

---

## Project Structure
-   `src/main/java/pt/challenge/client`: Feign-like clients for external service communication.
-   `src/main/java/pt/challenge/strategy`: Implementation of recommendation logic strategies.
-   `src/main/java/pt/challenge/config`: Configuration for security, resilience, cache, and OpenAPI.
-   `src/main/resources/db/migration`: SQL scripts for database schema management.
-   `local/`: Docker configuration and WireMock stubs.
