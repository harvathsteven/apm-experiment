# OpenTelemetry and Byte Buddy POC

This is a proof-of-concept application demonstrating the integration of OpenTelemetry and Byte Buddy in a Spring Boot REST API.

## Features

- REST API for managing users
- OpenTelemetry instrumentation for tracing
- Byte Buddy for method timing instrumentation
- Simulated database layer
- Distributed tracing visualization with Jaeger

## Prerequisites

- Java 17 or higher
- Maven
- Docker & Docker Compose

## Building and Running

1. Build the application:
```bash
mvn clean package
```

2. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

- `POST /api/users` - Create a new user
- `GET /api/users/{id}` - Get a user by ID
- `GET /api/users` - Get all users
- `DELETE /api/users/{id}` - Delete a user

## Example Usage

Create a new user:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

Get all users:
```bash
curl http://localhost:8080/api/users
```

## Observability

The application uses OpenTelemetry for tracing and Byte Buddy for method timing instrumentation. You can see:

1. OpenTelemetry traces in the console output
2. Method execution times in the console output (via Byte Buddy instrumentation)

## Distributed Tracing with Jaeger

You can visualize distributed traces using Jaeger and the OpenTelemetry Collector.

### Setup

1. Make sure Docker and Docker Compose are installed and running.
2. Start Jaeger and the OpenTelemetry Collector:
   ```bash
   docker-compose up -d
   ```
3. Start your Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
4. Generate some traces by calling the API endpoints (see Example Usage above).
5. Open the Jaeger UI in your browser: [http://localhost:16686](http://localhost:16686)
   - Select the service `otel-bytebuddy-poc` in the dropdown.
   - Click "Find Traces" to view traces.

### How it works
- The application exports traces using the OTLP exporter to the OpenTelemetry Collector.
- The Collector forwards traces to Jaeger for visualization.

## Architecture

- `UserController`: REST endpoints with OpenTelemetry instrumentation
- `UserService`: Business logic with OpenTelemetry instrumentation
- `UserRepository`: Simulated database layer with Byte Buddy instrumentation
- `User`: Data model
- `OpenTelemetryConfig`: Configuration for OpenTelemetry

## Project Setup

1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd apm-experiment
   ```
2. Follow the steps above to build, run, and visualize traces.

## ByteBuddy Runtime Instrumentation

This project uses [ByteBuddy](https://bytebuddy.net/) to instrument the `UserRepository` at runtime. When the application starts, ByteBuddy redefines the repository class so that all public, non-static methods are intercepted. The interceptor logs the execution time of each method call.

### How it works
- On application startup, ByteBuddy redefines `UserRepository` using a runtime agent.
- All public, non-static methods are intercepted by a static interceptor method.
- When you call any API endpoint that uses the repository, you will see log output like:
  
  ```
  2025-06-12 15:00:00 [http-nio-8080-exec-1] INFO  com.example.repository.UserRepository - Method save took 123456 ns
  ```

### Troubleshooting
- If you do not see timing logs after hitting repository endpoints, ensure:
  - The application is running with Java 17+.
  - You are using the provided `pom.xml` and have not removed ByteBuddy dependencies.
  - You are making requests to endpoints that use the repository (e.g., `/api/users`).
- If you see errors related to ByteBuddy or method delegation, ensure the interceptor method signature matches the repository methods (see `UserRepository.java`).

### Example usage
After starting the app, run:

```sh
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"name":"Alice","email":"alice@example.com"}'
```

You should see a log line in the console with the method timing. 