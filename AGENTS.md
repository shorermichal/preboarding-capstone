# Project Instructions

## Overview
- This is a Spring Boot REST API for managing tasks.
- Tasks are stored in memory without a database.
- The project uses Java 17 and Maven.

## Build and Test
- Run tests using `./mvnw test`.
- On Windows, use `.\mvnw.cmd test`.

## Coding Conventions
- Keep the code simple, readable, and maintainable.
- Controllers should handle HTTP requests and responses.
- Put complex business logic in service classes.
- Follow the existing project structure and naming conventions.

## Rules
- Every new endpoint must include MockMvc tests.
- Validate incoming requests and handle errors appropriately.
- Run all tests before completing a task.
- Do not commit code if tests are failing.