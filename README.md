# Naditech User Management Microservice

High-security user onboarding, authentication, and customer profile management microservice built with Spring Boot and Clean Architecture.

## Architecture

This microservice is built following **Clean Architecture** (Ports and Adapters):

- **Domain Layer**: Enterprise business rules, pure domain models (`User`, `RefreshToken`, `AuditLog`), domain exceptions, and secondary repository ports.
- **Application Layer**: Use cases (`RegisterUserUseCase`, `LoginUserUseCase`, `RefreshTokenUseCase`, `ValidateTokenUseCase`, etc.), input/output ports, commands, and results.
- **Infrastructure Layer**: Spring Data JPA persistence adapters, JJWT security provider adapter, BCrypt encoder adapter, API Gateway identity filter, and REST controllers.

## Ecosystem Integrations

- **API Gateway**: Requests forwarded with `X-User-Id`, `X-User-Email`, `X-User-Role` headers are authenticated directly via `GatewayHeaderAuthenticationFilter`. Token introspection is provided at `POST /api/v1/auth/validate`.
- **Core Service**: Internal service-to-service queries for user account lookups and transaction eligibility at `GET /api/v1/internal/users/**`.

## Getting Started

### Database
Start PostgreSQL via Docker Compose:
```bash
docker compose up -d
```

### Run Service
```bash
./mvnw spring-boot:run
```

### Swagger Documentation
Access interactive OpenAPI documentation at:
`http://localhost:8085/swagger-ui.html`

## Production Readiness
Verified with Spring Boot, PostgreSQL, and Clean Architecture.
