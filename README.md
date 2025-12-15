# Lab6 framework

This repository hosts the `lab6` Spring Boot service and static UI. Use the top-level Maven aggregator (this directory) to select the `lab6` module when running from the repo root.

## Running locally
- **From the repo root**: `mvn -pl lab6 spring-boot:run` (requires Java 21 and Maven 3.9.11). The `-pl lab6` selector must be used only at the root; if you `cd lab6`, run `mvn spring-boot:run` without `-pl`.
- The app serves both API and static frontend at `http://localhost:8080/`.

## Building and running with Docker Compose
1. `cd lab6`
2. `docker compose up --build`
   - Builds the application image with Maven 3.9.11 / Temurin 21.
   - Starts Postgres and wires credentials to the app via environment variables.
3. Once healthy, reach the UI at `http://localhost:8080/`.

## Building a standalone image
- From `lab6`: `docker build -t lab6-framework .`
- Run it alongside a Postgres instance (Compose is recommended to provide the database connection automatically).
