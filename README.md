# Product Recommendation Service

The **Product Recommendation Service** is a production-ready Spring Boot application designed to provide personalized product recommendations. It leverages modern Java features, asynchronous processing, and advanced resilience patterns to ensure high performance and reliability.

## 🚀 Features

- **Personalized Recommendations**: Intelligent filtering based on user profiles, preferences, and price ranges.
- **Asynchronous Parallel Processing**: High-performance catalog fetching using `CompletableFuture` and **Java 21 Virtual Threads**.
- **Distributed Tracing**: Automated **Correlation ID** management across logs for end-to-end traceability.
- **Resilience Design**: Advanced fault tolerance with **Circuit Breakers**, **Retries**, and **Bulkheads**.
- **Multi-Level Caching**: Optimized performance using **Caffeine** with specialized TTLs for volatile and non-volatile data.
- **Observability**: Built-in monitoring with **Prometheus** metrics and **Grafana** dashboards.
- **Clean Architecture**: Decoupled external clients and mappers adhering to **SOLID** principles.
- **JPA Auditing**: Automatic tracking of feedback creation time.

---

## 🛠 Tech Stack

- **Java 21** (with Virtual Threads enabled)
- **Spring Boot 3.4.3**
- **Spring Data JPA** & **PostgreSQL**
- **Resilience4j** (Fault Tolerance)
- **Caffeine** (Caching)
- **Flyway** (Database Migrations)
- **Prometheus & Grafana** (Monitoring)
- **WireMock** (External Service Simulation)
- **Swagger/OpenAPI 3** (API Documentation)

---

## 📖 Key Architectural Concepts

### 1. Resilience & Fallbacks
The service implements defensive programming to survive external service failures:
- **Circuit Breaker (Product Catalog)**: If the catalog service fails repeatedly, the circuit opens to prevent cascading failures.
- **Retry (User Profile)**: Automatically retries failed profile fetches to handle transient network issues.
- **Bulkhead (User Profile)**: Limits concurrent calls to the profile service to prevent resource exhaustion.
- **Fallbacks**: Both services provide graceful fallbacks (empty results or default profiles) so the core recommendation engine never crashes.

### 2. Caching Strategy
Configured in `CacheConfig.java` to balance performance and data freshness:
- `userProfiles`: **24-hour TTL** (Profiles change rarely).
- `products`: **2-minute TTL** (Prices and availability are highly volatile).

### 3. Monitoring & Observability
- **Prometheus**: Scrapes metrics from `/actuator/prometheus`.
- **Grafana**: Pre-configured dashboards to visualize throughput, latency, and resilience events.
- **Correlation ID**: Managed by `CorrelationIdFilter`. Every request gets a unique ID in the `X-Correlation-Id` header, which is propagated to logs via MDC: `[%X{correlationId}]`.

---

## 🚦 How to Run

### 1. Infrastructure (Docker)
Start the Database, WireMock, Prometheus, Grafana, and SonarQube:
```bash
cd local
docker-compose up -d
```

### 2. Run the Application
```bash
./gradlew bootRun
```

### 3. Verify the Tools
- **API Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Metrics (Prometheus)**: [http://localhost:9090](http://localhost:9090)
- **Monitoring (Grafana)**: [http://localhost:3000](http://localhost:3000) (User: `admin` / Pass: `admin123`)
- **External Mocks (WireMock)**: [http://localhost:8081/__admin](http://localhost:8081/__admin)
- **Code Quality (SonarQube)**: [http://localhost:9000](http://localhost:9000)

---

## 📝 API Endpoints

### Recommendations
`GET /api/recommendations/{userId}`
- **Security**: Requires HTTP Basic Auth (`admin`/`admin123`).
- **Traceability**: Pass `X-Correlation-Id` in headers to track the request.

### Feedback
`POST /api/recommendations/feedback`
- **Body**:
```json
{
  "userId": "user-1",
  "productId": "prod-123",
  "feedback": "LIKED",
  "comment": "Exactly what I was looking for!"
}
```

---

## 🧪 Quality & Testing

### Running Tests
```bash
./gradlew test
```
The project maintains high coverage with unit tests for logic and integration tests (WireMock) for external communication.

### Static Analysis
Run SonarQube analysis:
```bash
./gradlew clean jacocoTestReport sonar -Dsonar.login=admin -Dsonar.password=admin
```

### Exception Handling
Standardized error responses are managed by `GlobalExceptionHandler`:
- `400 Bad Request`: Validation or Constraint violations.
- `404 Not Found`: Missing resources from external APIs.
- `503 Service Unavailable`: Triggered when the Circuit Breaker is open.
- `500 Internal Server Error`: Unhandled exceptions.
