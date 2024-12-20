# Comfact Facade

_The service acts as a facade and provides an api to interact with an external service managed by the Comfact company, which is used to electronically sign documents._

## Getting Started

### Prerequisites

- **Java 21 or higher**
- **Maven**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/Sundsvallskommun/api-comfact-facade.git
   cd api-comfact-facade
   ```
2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   - Using Maven:

     ```bash
     mvn spring-boot:run
     ```
   - Using Gradle:

     ```bash
     gradle bootRun
     ```

## Dependencies

This microservice depends on the following services:

- **Comfact e-signing service**
  - **Purpose:** The service is used for managing the e-signing procedure between issuer and executor of the signing
  - **Repository:** External service managed by [Comfact company](https://www.comfact.se/en-us)
- **Party**
  - **Purpose:** Service is used for transating party id to legal id
  - **Repository:** [https://github.com/Sundsvallskommun/api-service-party](https://github.com/Sundsvallskommun/api-service-party)
  - **Setup Instructions:** See documentation in repository above for installation and configuration steps.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Alternatively, see the `openapi.yml` file located in directory `/src/integration-tests/resources` for the OpenAPI specification.

## Usage

### API Endpoints

See [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X 'GET' 'http://localhost:8080/2281/signings/1e4d6a9667eb4e7bba0cd6189f70317b'
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

  ```yaml
  server:
    port: 8080
  ```
- **External Service URLs:**

  ```yaml
  integration:
    comfact:
      url: http://dependency_service_url
    party:
      url: http://dependency_service_url

  spring:
    security:
      oauth2:
        client:
          provider:
            party:
              token-uri: http://token_url
              client-id: some-client-id
              client-secret: some-client-secret
  ```
- **Additional configuration:**

  ```yaml
  municipality:
    ids: 
      <municipalityId>:
        username: comfact-username-for-municipality
        password: comfact-password-for-municipality
  ```

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-comfact-facade&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-comfact-facade)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-comfact-facade&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-comfact-facade)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-comfact-facade&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-comfact-facade)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-comfact-facade&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-comfact-facade)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-comfact-facade&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-comfact-facade)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-comfact-facade&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-comfact-facade)

## 

&copy; (c) 2024 Sundsvalls kommun
